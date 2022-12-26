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
    UserID INT NOT NULL REFERENCES users (UserID),
    CardName VARCHAR(255) NOT NULL,
    Damage INT NOT NULL,
    Paused BOOL NOT NULL
);