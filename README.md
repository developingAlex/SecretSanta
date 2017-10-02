# SecretSanta

This app was made when I was joining in on a [secret santa](https://en.wikipedia.org/wiki/Secret_Santa) one year and we were pulling our names literally out of a hat (and written on paper) but had to redo the rounds multiple times because someone would end up getting themselves.

This app aims to solve that issue by allowing a phone to be passed around instead of a hat.

This app is an appropriate substitute for a physical hat/bucket with everyones names in it where everyone is in the same room, if it's not possible to get everyone in the same place then there are already existing secret santa emailing services that you can find online to get your recipient secretly emailed to you.

## How to use

Simply start by entering the names of each individual participating in the round, as individuals are added you will see a list populate with their names. When everyone has been added click **Done**.

The app will then cycle through each of the names and this is where the phone is passed between everyone. If I was involved and I was the first name in the round then it would address me and instruct me to press a button to see who I got. I would make sure no one can see the screen as I read the name. Once I've memorised who I have I would press a button to clear that name from the screen and see who I should pass the phone to next.

## How it works

Quite simply the app will randomly assign a name with another name, if it's the same name it will generate again before displaying to the user, it keeps track of which names have already been *dispensed* and when only two names remain the randomness becomes more manipulated so as to prevent the possibility of the last person being left with no other name in the hat except their own. Before this was fixed to be this way there were some times when the last person would get themselves so it would *redraw* but there would be no other possible result except themselves again so it would end up in an endless loop. This check for the second last person fixes that.

## Still to do

- Improve greatly the user interface design.


