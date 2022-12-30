CREATE TABLE users (
    UserID serial PRIMARY KEY,
    Username VARCHAR(255) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL,
    Coins INT,
    Scores INT,
    GamesPlayed INT,
    Bio VARCHAR(255),
    Image VARCHAR(255),
    AuthToken VARCHAR(255)
);

CREATE TABLE cards (
    CardID VARCHAR(255) PRIMARY KEY,
    UserID INT NOT NULL REFERENCES users (UserID),
    CardName VARCHAR(255) NOT NULL,
    Damage INT NOT NULL,
    Paused BOOL NOT NULL
);

CREATE TABLE packages (
    CardID VARCHAR(255) PRIMARY KEY,
    UserID INT NOT NULL,
    CardName VARCHAR(255) NOT NULL,
    Damage INT NOT NULL,
    Paused BOOL NOT NULL
);

CREATE TABLE decks (
    CardID VARCHAR(255) PRIMARY KEY,
    UserID INT NOT NULL REFERENCES users (UserID),
    CardName VARCHAR(255) NOT NULL,
    Damage INT NOT NULL,
    Paused BOOL NOT NULL
);

CREATE TABLE tradingDeals (
    TradeID VARCHAR(255) PRIMARY KEY,
    UserID INT NOT NULL REFERENCES users (UserID),
    CardToTradeID VARCHAR(255) NOT NULL REFERENCES cards (CardID),
    MinDamage INT,
    Type VARCHAR(40)
);

CREATE TABLE battles (
    BattleID serial PRIMARY KEY ,
    Requester VARCHAR(255) NOT NULL REFERENCES users (Username),
    Acceptor VARCHAR(255) NULL REFERENCES users (Username),
    Ended BOOLEAN DEFAULT FALSE
);