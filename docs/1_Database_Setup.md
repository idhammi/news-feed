# Database & Caching (Room)

This project uses local data storage to support offline caching via **Room Database** from Android Jetpack.

## 1. Database Schema

- **Database Name**: `newsfeed.db`
- **Table**: `articles`

### `articles` Table
Stores news articles retrieved from NewsAPI for offline availability.

| Column Name | Data Type | Description | Key |
| :--- | :--- | :--- | :--- |
| `id` | TEXT | Unique identifier for the article (mapped to the URL) | Primary Key |
| `title` | TEXT | News article title | - |
| `description` | TEXT | Short description or excerpt of the news | - |
| `url` | TEXT | Original article URL link | - |
| `urlToImage` | TEXT | URL to the news cover/thumbnail image | - |
| `publishedAt` | TEXT | Publication time in ISO-8601 format | - |
| `content` | TEXT | Full content of the news | - |
| `country` | TEXT | Country code of the news (e.g., 'id' or 'us') | - |

## 2. Caching Strategy

The project implements a **Single Source of Truth (SSOT)** pattern using `RemoteMediator` from the Paging 3 library:
1. Data is initially loaded from the local Room Database to reduce load times and enable offline access.
2. A background network request is made to `newsapi.org` to fetch the latest data.
3. Upon a successful response, existing cached articles are cleared (during a refresh operation) and replaced with the new data.
4. The UI automatically recomposes by observing a data stream (Flow) from the Room DAO.

## 3. SQL Schema
While Room abstracts the SQL generation, the underlying schema for the `articles` table is represented as follows:
```sql
CREATE TABLE IF NOT EXISTS `articles` (
    `id` TEXT NOT NULL, 
    `title` TEXT NOT NULL, 
    `description` TEXT, 
    `url` TEXT NOT NULL, 
    `urlToImage` TEXT, 
    `publishedAt` TEXT NOT NULL, 
    `content` TEXT, 
    `country` TEXT NOT NULL, 
    PRIMARY KEY(`id`)
)
```
