# poker-game
A simple single player poker simulator.

## Access to the Game
### Log In
The user is greeted by a **log in screen** where he must enter a **username**, not already taken by another player, in order to access and play. The various players, all with different names, are saved in a **.json file** with their username and **balance**, initially set to 100000. From the top menu you can view the statistics of the memorized players.

### Game Lobby
Once logged in, the user will see the **game lobby**, with information on the selected player on the left and the icon of the **poker table** to sit at on the right. From the top menu in the 'Edit' section you can change the selected player, returning to the login screen. By pressing the 'Play' button the user will seat at the poker table and the game will begin.

## Game Rules
The rules are those of [texas hold'em poker](https://en.wikipedia.org/wiki/Texas_hold_%27em). In short, each game begins with the mandatory bets (the **small** and **big blind**), then 2 cards are dealt to each player and the first round of betting begins, starting with the player to the left of the big blind. A round of betting ends when only one player remains (the others have folded) or all players have bet the same amount. After the first round of betting, 3 cards are revealed on the board, the **flop**, and the second round of betting begins, starting from the player to the left of the dealer. Then another card is revealed, the **turn**, and a new round of betting begins. Finally a fifth card is turned over on the table, the **river**, and the final round of betting takes place. After this all the remaining players show their cards and whoever has the [best combination](https://en.wikipedia.org/wiki/Texas_hold_%27em#Hand_values) wins the pot, in case of a tie the pot is divided equally. In the event that a player goes **all in** (i.e. bets his entire balance) the following players can only **call** his bet or **fold**. If, on the other hand, a player fails to call a bet with all of his balance, a **side pot** will be created in which the surplus will be placed; side pots can only be won by players who bet on them.

### Bots and Graphical Implementation
The user will play against 3 **bot players**, whose names and balances (along with the user's) are visible above their respective cards. Pressing the arrow button at the bottom right will advance the game by one turn. When it will be the user's turn, he will be able to choose his own action by pressing the appropriate buttons to the right of his cards. In the text box on the top right you will have a textual description of the actions performed by all players, as well as the value of their hands at **showdown**. Bot players with empty balance are automatically replaced during the game, if it is the user's player who empty the balance then you will automatically be returned to the game lobby. To get up from the table, the user can press the toggle button at the bottom right, in this way, before the start of the new game, the user will **leave the table**; it is not possible to leave the table in the middle of a game.

## Exit the Game
When the user decides to leave the table or when his balance runs out, he will return to the game lobby and from there he can close the game or (unless he has an empty balance) go back to the poker table. Players who have lost all of their balance are **deleted** from the .json file and then their name can be used again.
