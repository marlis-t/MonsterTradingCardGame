# MonsterTradingCardGame
Semesterproject of the Course "SoftwareArchitecture"

Implements a multi-threaded server listening for Http-Requests that connects to a Postgresql Database hosted in a Docker Container.

Paths:
GET:
-/users/{username} -> returns information about user {username}
-/cards -> returns all cards of a user
-/decks -> returns deck of a user
-/stats -> returns stats of a user
-/scores -> returns a scoreboard with users ranked by their score
-/tradings -> returns all currently available trading deals

POST:
-/users -> creates a new user
-/sessions -> logs in a user, returns a newly generated authToken
-/packages -> creates a new package (5 cards)
-/packages/transactions -> adds a package to a user's cards
-/battles -> creates a battle request
-/tradings -> creates a new trading deal
-/tradings/{tradingDealId} -> carries out the trading deal {tradingDealId}

PUT:
-/users/{username} -> updates information of user {username}
-/decks -> sets a user's deck

DELETE:
-/tradings/{tradingDealId} -> deletes trading deal {tradingDealId}

Requirements:
-written in Java
-does not use any Http-Helper-Frameworks
-uses a psql database
-multithreading
-does not allow SQL-Injections
-Unit Tests

All requests this project can complete are listed in "MonsterTradingCards.exercise.curl.withpause.bat"
