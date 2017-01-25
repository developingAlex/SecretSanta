package com.example.user1.secretsanta;

import android.app.Activity;
import android.app.Application;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class acts as the holder of the applications current state. It manages the contents of the
 * 'hat' and at what point in the exercise we're upto (adding names to the hat or dispensing the
 * results)
 * <p>
 * The reason the bulk of the code sits in this class as opposed to the MainActivity class is so
 * that we can rely on the onCreate() of this class to know when the application is being executed,
 * as opposed to just the user rotating the device resulting in the screen being updated.
 */
public class ApplicationStateClass extends Application {

    final boolean DEBUGGING = false;

    // Constants for the stage variable:
    final int WRITING_DOWN_NAMES_FOR_THE_HAT = 0;
    final int SHUFFLING_HAT = 1;
    final int DISPENSING_RESULTS = 2;

    /*number of consecutive times that a user draws their own name out of the hat before prompting a
    * complete retry, everyone throws their names back into the hat and they get shuffled again.*/
    String dispensingText; /*holds the current label being used on the
    dispensing text label (the label that shows the list of names being built and also reads the
    results to each person.  */
    boolean doneButtonsEnabledStatus,nextButtonsEnabledStatus,nameEntryFieldsEnabledStatus; /*the
    enabled status of the next and done buttons and the name entry field, for intuitive user
    feedback these elements are at times disabled visually indicating to the user that it is
    currently an inappropriate time to use them. */
    public ArrayList<String> namesList; /*the list of names that are thrown in the hat, list is
    populated as the user clicks the next button*/
    public ArrayList<String> listOfPeoplesNamesAlreadyDrawnFromHat; /*list of names that have been
    drawn from the 'hat', this list is populated as the results are randomly determined.*/
    public int uptoName,finishedPrompt;/*variables that indicate what point we're upto in the
    dispensing of results.*/
    public int stage; //stage 0: user enters names of people
    //stage 1: app randomises peoples partners
    //stage 2: app dispenses results to each person one at a time
    public boolean paperHasBeenUnwrappedAndRead; // indicates the state we're upto when a user is pressing the done button.
    //0: the text gets set to address the next person in the list to press the
    // done button to see their secret santa
    //1: the text gets set to be that persons secret santa.
    public Boolean finishedRound;
    Activity mainActivity;

    /*
    *This is run only once, upon initially opening the app. */
    @Override
    public void onCreate(){
        super.onCreate();
        resetGame(true); //call reset game (with true) to intialise variables.
    }

    /**
     * Analagous to writing a name on a piece of paper, scrunching up that piece of paper and then
     * adding it to the hat, but with the addition of an omnipotent being that checks that there is
     * actually a name written on the 'piece of paper' and that it is unique.
     *
     * @param name the name written on the 'piece of paper'
     */
    public void addNameToHat(String name){

        if (DEBUGGING) System.out.println("enteredName="+name);
        //TextView lowerTextLabel = (TextView) mainActivity.findViewById(R.id.dispensingTextView);
        if (DEBUGGING) System.out.println("stage="+stage);

        if(name.length() == 0 ){
            displayQuickToastMessage(getString(R.string.toastmsg_attempt_to_add_no_name));
        }else if( namesList.contains(name) ){
            displayQuickToastMessage(getString(R.string.toastmsg_attempt_to_add_name_again));
        }else{
            namesList.add(name);
            ((EditText) mainActivity.findViewById(R.id.enter_name_field)).setText("");
            setDispensingText(dispensingText + ", " + name);
            //lowerTextLabel.setText(lowerTextLabel.getText()+","+name);
            if( namesList.size() == 3 ){ //enable the Done button only once the user inputs the third person.
                enableDoneButton(true);
            }
        }
    }
/**
 * Resets the system state to that when initialised ready for a new round, also performs application
 * initialisation.
 *
* @param    initialSetup    indicates if application creation (true) or new round (false)*/
    public void resetGame(boolean initialSetup){
        assert(mainActivity != null);
        namesList = new ArrayList<String>();
        listOfPeoplesNamesAlreadyDrawnFromHat = new ArrayList<String> ();
        stage = WRITING_DOWN_NAMES_FOR_THE_HAT;
        uptoName = 0;
        paperHasBeenUnwrappedAndRead = false;
        finishedRound = false;
        finishedPrompt = 0;
        if(initialSetup){
            doneButtonsEnabledStatus = false; //will be applied when MainActivity runs its onCreate()
            nextButtonsEnabledStatus = true; //will be applied when MainActivity runs its onCreate()
            dispensingText = "..."; //will be applied when MainActivity runs its onCreate()
            nameEntryFieldsEnabledStatus = true;
        }else {
            enableDoneButton(false);
            enableNextButton(true);
            enableNameEntryField(true);
            setDispensingText("...");
            if (DEBUGGING) System.out.println("Secret Santa tool has been reset");
        }
    }

    /**
     * called by mainActivity when it's created/redrawn (screen rotates).
     * @param   mainActivitysCurrentInstance    the MainActivity object.
     * */
    public void updateInstance(Activity mainActivitysCurrentInstance){
        this.mainActivity = mainActivitysCurrentInstance;

        //ensure the buttons and name entry edittext are enabled appropriately:
        ((Button) mainActivity.findViewById(R.id.DONEButton)).setEnabled(doneButtonsEnabledStatus);
        ((Button) mainActivity.findViewById(R.id.NEXTButton)).setEnabled(nextButtonsEnabledStatus);
        ((EditText) mainActivity.findViewById(R.id.enter_name_field)).setEnabled(nameEntryFieldsEnabledStatus);

        //ensure the dispensing text which is dynamic is restored appropriately:
        ((TextView) mainActivity.findViewById(R.id.dispensingTextView)).setText(dispensingText);
    }


    /**
     * Performs the relevant action depending on what stage we're upto when the user presses the
     * DONE button. If the user had just been
     * entering names building up the list of people then the process of dispensing the results begins.
     * if the dispensing of results is already underway then it sets the text appropriately.
     * Finally when the last result has been dispensed, continual pressing of the DONE button will
     * invoke the secret santa app to reset itself.
     */
    public void doneButtonPressed(){

        assert(mainActivity != null);

        if (stage == WRITING_DOWN_NAMES_FOR_THE_HAT){
            enableNextButton(false);//the time for entering of names has ended
            enableNameEntryField(false);
            stage = SHUFFLING_HAT;
            //begin randomisation of partners.
            RandomlyGeneratePartnershipsAndPopulatePartnersList();
            stage = DISPENSING_RESULTS;
        }

        if(finishedRound ){
            resetGame(false);
        }else if (stage == DISPENSING_RESULTS && uptoName < namesList.size()){ //use the value of variable
            // 'uptoName' to determine which name to display next, initially this value is 0.

            if (paperHasBeenUnwrappedAndRead){
                if (uptoName < namesList.size()-1) {
                    setDispensingText("You have " + listOfPeoplesNamesAlreadyDrawnFromHat.get(uptoName) + ", press DONE once you've memorised this, then pass this to " + namesList.get(uptoName + 1) + ".");
                }else{ //in this case we're on the last person so the message cannot say to pass to another after.
                    setDispensingText("You have " + listOfPeoplesNamesAlreadyDrawnFromHat.get(uptoName) + ", press DONE once you've memorised this to clear it from the screen.");
                }
                uptoName++;
                paperHasBeenUnwrappedAndRead = false;
            }else {
                setDispensingText(namesList.get(uptoName)+" press DONE to see your secret santa.");
                paperHasBeenUnwrappedAndRead = true;
            }

            if (uptoName == namesList.size()){
                finishedRound = true;
                displayQuickToastMessage(getString(R.string.toastmsg_1st_popup_after_a_finished_round));
            }
        }
    }

    /**
     * Randomly assigns secret santas by building a list of the namelist's indexes in a shuffled
     * order. that then represents the assignments, eg. nameslist is {alex, aaron, arnold}
     * listOfPeoplesNamesAlreadyDrawnFromHat could become {aaron, arnold, alex}, here they are
     * together:
     * {alex, aaron, arnold} <-- nameslist (the person buying the present)
     * {aaron, arnold, alex} <--- shuffled list (the person to buy a present for)
     *
     * so that means, alex has aaron, aaron has arnold and arnold has alex.
     *
     * operates on the global variables in this class, and should only be called if the namesList
     * arraylist is at least 3 people full.
     */
    private void RandomlyGeneratePartnershipsAndPopulatePartnersList(){
        assert(namesList.size() > 2);
        listOfPeoplesNamesAlreadyDrawnFromHat.clear();
        ArrayList<Integer> listOfDrawnIndexes = new ArrayList<Integer>();
        ArrayList<Integer> listOfIndexesRemaining = new ArrayList<Integer>();
        int i = 0;

        while ( i < namesList.size() ){ //initialise listOfIndexesRemaining to {1,2,3,...,n}
            listOfIndexesRemaining.add(i);
            i++;
        }

        i = 0;
        int numberOfFailedAttemptsToAssignPartner = 0;
        Random randomGenerator = new Random();

        while (i < namesList.size()){

            int randomIndex = randomGenerator.nextInt(listOfIndexesRemaining.size()); //come up with a number between 0 and the list size.
            if(DEBUGGING) System.out.println("randomIndex="+randomIndex);
            assert (randomIndex < listOfIndexesRemaining.size());
            //int indexOfDrawnName = listOfIndexesRemaining.get( randomIndex % listOfIndexesRemaining.size() );
            int indexOfDrawnName = listOfIndexesRemaining.get( randomIndex );
            if (indexOfDrawnName == i){ //they drew themselves...
                indexOfDrawnName = listOfIndexesRemaining.get( (randomIndex + 1)%listOfIndexesRemaining.size() ); //...so just give them what comes next
            }
            if(DEBUGGING) System.out.println("indexOfDrawnName="+indexOfDrawnName);
            if(DEBUGGING) System.out.println("listOfIndexesRemaining.size()="+listOfIndexesRemaining.size());
            if(DEBUGGING){
                int j = 0;
                String listToPrint = "";
                while (j<listOfIndexesRemaining.size()){
                    listToPrint += listOfIndexesRemaining.get(j)+",";
                    j++;
                }
                System.out.println("contents of listOfIndexesRemaining:"+listToPrint);

                j = 0;
                listToPrint = "";
                while (j<listOfDrawnIndexes.size()){
                    listToPrint += listOfDrawnIndexes.get(j)+",";
                    j++;
                }
                System.out.println("contents of listOfDrawnIndexes:"+listToPrint);
            }
            int indexOfSecondLastPerson = namesList.size()-2;
            int indexOfLastPerson = namesList.size()-1;
            boolean theLastPersonHasntYetBeenDrawnFromHat = !listOfDrawnIndexes.contains(indexOfLastPerson) && indexOfDrawnName != indexOfLastPerson;
            boolean weAreUptoTheSecondLastPerson = (i == indexOfSecondLastPerson);
            if(weAreUptoTheSecondLastPerson && theLastPersonHasntYetBeenDrawnFromHat){
             /* this will ensure that the last person to draw from the hat
                is never left with only their own name left in the hat */
                indexOfDrawnName = indexOfLastPerson;
                if(DEBUGGING) System.out.println("we just prevented the situation where the last name in the hat was also the name of the last person.");
            }
            assert (indexOfDrawnName < namesList.size());
            assert ( !listOfDrawnIndexes.contains(indexOfDrawnName) );
            assert (i != indexOfDrawnName);
            listOfIndexesRemaining.remove( (Integer) indexOfDrawnName);
            listOfDrawnIndexes.add(indexOfDrawnName);
            listOfPeoplesNamesAlreadyDrawnFromHat.add(namesList.get(indexOfDrawnName));
            i++;

        }
        if (DEBUGGING) System.out.println("Successfully generated random partnerships");
    }



    /**
     * enables or disables the Done button based on the boolean provided for enabled.
     * Ensures that the status is saved in the event that a MainActivity redraw requires the
     * button's enabled status to be restored.
     *
     * @param enabled the status to set the Done button's enabled property to.
     */
    public void enableDoneButton(boolean enabled){
        doneButtonsEnabledStatus = enabled;
        ((Button) mainActivity.findViewById(R.id.DONEButton)).setEnabled(doneButtonsEnabledStatus);
    }

    /**
     * Enables or disables the Done button's clickability based on the boolean provided for enabled.
     * This is in contrast to enableDoneButton(boolean) because this only makes the button not able
     * to be clicked, though it will still look like it can be clicked. In contrast to the Enabled
     * property which changes the actual appearance of the button to make it look like it cannot be
     * clicked.
     *
     * The purpose of this function is to prevent accidental quick successive taps of the Done
     * button by users which would cause a user to either miss who they had, or see who the next
     * person had resulting in the game being ruined and the need to start it all over again.
     *
     * It is also cleaner to not have the button appear to be changing upon being pressed and then
     * 'coming back online' or otherwise 'lagging', such a feature is inteded to be transparent to
     * the users.
     * @param enabled the status to set the Done button's clickable property to.
     */
    public void setClickableDoneButton(boolean enabled){
        ((Button) mainActivity.findViewById(R.id.DONEButton)).setClickable(enabled);
    }

    /**
     * enables or disables the Next button based on the boolean provided for enabled.
     * Ensures that the status is saved in the event that a MainActivity redraw requires the
     * button's enabled status to be restored.
     *
     * @param enabled the status to set the Next button's enabled property to.
     */
    public void enableNextButton(boolean enabled){
        nextButtonsEnabledStatus = enabled;
        ((Button) mainActivity.findViewById(R.id.NEXTButton)).setEnabled(nextButtonsEnabledStatus);
    }

    public void enableNameEntryField(boolean enabled){
        nameEntryFieldsEnabledStatus = enabled;
        ((EditText) mainActivity.findViewById(R.id.enter_name_field)).setEnabled(nameEntryFieldsEnabledStatus);
    }

    /**
     * Sets the dispensing text label (the label that displays the list of names entered and then
     * also acts as the label that tells people who they have) to the value provided.
     * @param textToDisplay the text to set the label to.
     */
    public void setDispensingText(String textToDisplay){
        dispensingText = textToDisplay;
        ((TextView) mainActivity.findViewById(R.id.dispensingTextView)).setText(dispensingText);
    }


    private void displayQuickToastMessage(String msg){
        Toast.makeText(this.getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }
    private void displayLongToastMessage(String msg){
        Toast.makeText(this.getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }

}

