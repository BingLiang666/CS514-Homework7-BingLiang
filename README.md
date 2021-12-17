# Music Garden
This is a music management tool for music database that contains 3 tables: songs, artists and albums.

It offers users a various of ways to 1) diaplay detailed music information 2) import music entities into database 3) automatically fill in the missing details of music entities using AudioDB(for artist and album) and MusicBranz(for song) 4) genetate and manage the playlist in different ways.

While it is running, some data in the database will be dynamically updated if any change happens, such as the "nSongs" and the "nAlbums" in the table "artist" and the "nSongs" in the table "album".

## Running in IntelliJ
1. Copy http URL in Github repo
2. Open IntelliJ File -> New -> Project From Version Control -> paste the URL you just copied in the URL blanket -> Click Clone
3. Go to src/main/java/edu.usfca.cs SQLite.class
4. Run SQLite.main()
5. Follow the prompts to play with it :)

Once the program is running, we will see the following interface in the console:

    Hello! Welcome to our Music Garden.
    There are a number of options you can choose to play with it.
    ------------------------MAIN MENU------------------------
    -1- Check All Songs, Artists, Albums In The Database.
    -2- Check A Specific Part Of The Database.
    -3- Import A Song Into The Database Using SONG NAME.
    -4- Import An Artist Into The Database Using ARTIST NAME.
    -5- Import An Album Into The Database Using ARTIST NAME And ALBUM RELEASE DATE.
    -6- Generate A Playlist.
    -7- Shuffle A Playlist.
    -8- Exit.
    Your choice:
  

The followings are detailed descriptions of its functions and how to use them:

## 1. Diplay

   1.1 Display the whole database(i.e. songs, artists and albums)
   
    Choose -1- in the Main Menu.

   1.2 Display a part of database
   
    Choose -2- in the Main Menu.

   1.2.1 Display a part of songs
   
    Choose -1- in the prompt "What kind of information would you prefer to see?".
    
   1.2.1.1 Display songs released after certain year
   
    1) Choose -1- in the prompt "Songs After Certain Year.". 
    2) Enter an integer in valid format representing the year.
  
   1.2.1.2 Display songs owned by certain artist
   
    1) Choose -2- in the prompt "Songs From Certain Artist.".
    2) Enter a string representing the artist name.

   1.2.1.3 Display songs stored in certain album
   
    1) Choose -3- in the prompt "Songs From Certain Album.".
    2) Enter a string representing the artist name.

   1.2.2 Display a part of artists

   1.2.2.1 Display artists with certain style
      
     1) Choose -1- in the prompt "Artists with certain style.".
     2) Enter a string representing the artist style.

   1.2.2.2 Display artists from certain area
   
     1) Choose -2- in the prompt "Artists from certain area.".
     2) Enter a string representing the area.
   
   1.2.3 Display a part of albums
   
   1.2.3.1 Display albums owned by certain artist
   
    1) Choose -2- in the prompt "Albums from certain artists.".
    2) Enter a string representing the artist name.
      
   1.2.3.2 Display albums in certain genre
   
    1) Choose -2- in the prompt "Albums in certain genre.".
    2) Enter a string representing the album genre.
    
## 2. Import

  2.1 Import A Song Into The Database Using SONG NAME.
     
     1) Choose -3- in the Main Menu.
     2) Enter the song's name that we want to import.
     3) If there is duplicated song in the database, we will be prompted to indicate whether the new song should be imported or not. 
     (NOTE: A song will be imported only if there are correct artist and album linked with it.)
     
  2.2 Import An Artist Into The Library Using ARTIST NAME.  
  
     1) Choose -4- in the Main Menu.
     2) Enter the artist's name whom we want to import.
     3) If the artist is already in the database or could not be found in the AudioDB, the artist will not be imported.
     
  2.3 Import An Album Into The Library Using ARTIST NAME And ALBUM RELEASE DATE
  
     1) Choose -5- in the Main Menu.
     2) Enter the name of the artist who owns the album that we want to import.
     3) If the artist is in the database, then enter an integer in valid format representing the release year.
     
## 3. Playlist

  3.1 Generate a playlist.
  
  3.1.1 By likes of song.
  
    1) Choose -1- in the prompt "In which way would you prefer to generate the playlist?".
    2) Enter an integer between 1~5 representing the likes of a song for the first time.
    3) Enter an integer between 1~5 indicating the lowest value of likes that all songs in the playlist have.
    (Note. Songs in the playlist will be sorted by their likes from the highest to lowest.)
  
  3.1.2 By release year of song.
  
    1) Choose -2- in the prompt 
     1) Choose -6- in the Main Menu.
     
     1) Choose -7- in the Main Menu.
     
     1) Choose -8- in the Main Menu.
     
