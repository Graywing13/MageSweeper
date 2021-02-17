# Magesweeper

## Overview 
*(For Marking, Project Phase 1.2)*

My project is a  **minesweeper boss fight game** where you are a mage fighting a dragon 
while avoiding explosive mines with unknown locations.
This game can be **used by people wanting a small break from productivity** 
as each game is expected to take around 2 minutes.
I am **interested in this project because I love strategy and role-playing games**, 
as well as **fun coding challenges**!

## User Stories 
*(For marking, Project Phase 1.3)*
- **As a user, I want to be able to add multiple unique GameMap objects with their unique layouts to my World object.**
- As a user, I want to be able to play against 3 different dragons which have different attack patterns.
- As a user, I want to be able to be able to choose between three playing board difficulties (marked by no. of mines).
- As a user, I want to be able to set my mage's name and my world's name.
- **As a user, I want to be able to choose to save my game progress to a save file before I exit the program**
- **As a user, I want to optionally be able to load a world I had previously played when the program starts.**

##Instructions for Grader
*(For marking, Project Phase 3)*

*Note: after adding the events, you can view them by:* Selecting option 3 (View Districts and High Scores)

**Add X to Y events:** (First + Second) 
- You can generate the first "Add X to Y" event by doing: 
Setup Default World > [Next] **Button** > Select option 5 of the **JComboBox** (add new district) > [Next] > [Next] > [Next]
- You can generate the second "Add X to Y" event by doing:
Select option 5 > [Next] > [Next] > [Next]

You can also generate an "Add X to Y" by selecting [Quick Select] in the **JMenu** > [Create new random district]

**Visual Component:** 
- You can see a background image on my main menu. 

**Audio Component:** 
- You can trigger a bloop sound when pressing any [Next] button. 
Please see "Add X to Y events" for a way to locate a [Next] button.

**Persistence:** (Save + Load)

You can save the game world* by doing either of the following:
- Go to the world menu > Select option 1 (save current world) 
- Press the exit button on the main (gray / beige) frame > select "Yes" when asked if you would
like to save game state

You can load your previously saved game world by:
1. Running the program > on the beige screen, select "Play pre-existing world" > [Next]
2. If you play or delete your existing districts, 
you'll notice that you can play and delete the maps you played and saved previously.

*\* Game world = The maps you played in the world and your best clear time for each map*

## Phase 4: Task 2 (Java Language Constructs)
- My construct: Usage of a Type Hierarchy 
  - My abstract class **LifeForm is the superclass to two subclasses: (1) Dragon (2) Mage**
  - A **method that the subclasses override is attackOpponent()**
  - The subclasses have distinct functionality because they represent life forms that MUST fight against each other
  - The subclasses extend the same superclass because they share functionality related to moving on the game board
  
## Phase 4: Task 3 (Coupling and Cohesion)
- In MagesweeperAppSwing, cohesion can be improved by putting the function which lists all available districts in 
GameWorld because GameWorld is in charge of keeping track of what districts it has
- In InGameTrackerEventsSwing and InGameTrackerEvents, the latter is used in the constructor of the former even though
 only a few fields shared, resulting in high coupling. Additionally, there are fields and methods with similar names and 
 functionalities. Coupling can be reduced by extracting a common abstract class. 

<br>
-----------------------
-----------------------
-----------------------

*Note: Anything below this line of text is either for the user's interest only 
or for completion if the programmer (BluePoisonDartFrog) has time, and **not** for the CPSC 210 Course.*

## Additional User Stories 
*(Not for Marking)*
- As a user, I want to be able to attack and be attacked by a "dragon" that is represented by a marker on a grid.
- As a user, I want to be able to enter a lose state if I get attacked too much, hit the time limit, or detonate a mine.
- As a user, I want to be able to enter a win state if I successfully bind the dragon by attacking it enough times.
- As a user, I want to be able to save which maps I have cleared and the shortest time it took to clear them.

## Storyline 
*(Not for Marking)*
As the omega of the most esteemed mage school in your world, 
you have taken on the daunting task of binding three dangerous dragons to prove your worth.
You have only one skill, Force Shield, can block you from all dragons' attack patterns,
but you are unable to move or attack during the skill and it takes time for the skill to be prepared.
This skill is unique to you, rendering you the only hero able to save your world from these havoc-wreaking dragons.
\
\
However, these three dragons can attack and lower your HP; if you take too much damage, you will have to retreat.
Additionally, they are only bindable in their dwellings,
caverns filled with underground tunneling mines from ancient scientists' failed attempt to weaken the dragons; 
the dragons cannot detonate mines because they always only fly or hover.
Unfortunately, you don't fly, so you would instantly be injured if you stepped into any live mines 
and would yet again be forced to retreat.
\
\
Thankfully, you have the scientists' Mine Controller which can 
tell you the total amount of mines in the caverns and the amount of mines in the 8 tiles around you.
The controller can also disable the mines' ability to move around for the duration of a "pacify period"
and deploy a contact defuser on the next tile you move onto, changing the tile to a mine-free safe square. 
However, there's a catch: because the controller is so old, 
its software has mutated to detonate all the mines after the pacify period has ended, meaning you have to act fast.
\
\
Equipped with your skill Force Shield, the Mine Controller, and the standard dragon binding spells, 
you set out on this perilous but rewarding journey!
***Your task is to bind the dragon without stepping in any of the mines before the time limit!***