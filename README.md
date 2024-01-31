[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/_Od3b_Hk)


# **Nirvana**

The Nirvana app is designed to be a travel companion for planning and exploration. Whether you're currently travelling or you want to plan your vacation, our app aims to enhance your travel experience by providing insights and recommendations.


## Contributions

* **Markus**:
   * Worked on the API
  
   * Worked on the backend for:
     * Home
       * Get current location
       * Get nearby sights, restaurants and shopping options
       * Get their location details
     * Profile 
     * Settings
     * Exploring
     * Recommendation

* **Alea**:
   * Worked on the frontend for:
     * Start
     * Sign Up
     * Log In
     * Home
     * Profile
     * Settings
     * Exploring
     * Recommendation
     
   * Created the bottom navigation bar including the navigation between the different views
     
     * Worked on the backend for:
       * Sign Up
       * Log In

All in all, we put the same amount of time and effort in and helped each other out when we had problems.


## Features

1. ### Location Exploration:
   * Users can set their current location or choose a different destination to explore
   * The app offers a list of nearby sights, restaurants, and shopping options

2. ### User Profile:
   * Users can create a profile to save their personal data

3. ### Settings:
    * Users can select location preferences and set up push notifications for weekly reminders

4. ### Detailed Activity Overview:
   * Get insights, including ratings, location and tags for a description

5. ### Database:
   * The app uses _Firebase_ to store user data and location information


## Development Process

* October: Came up with a project idea
* November: Started to design our mockup (_Nirvana.fig_ in our project folder)
* December: _Because of exams and christmas break, we didn't work on the project_
* January: Coded the whole project


## API
We use the Amadeus for Developers API. <br>
Because we only use the free version (Self-Service-API) we can’t use all features of the API. <br>
We planned to use the activity call and get data like the following example (from Postman): <br>
    { <br>
        "type": "activity", <br>
        "id": "6378382", <br>
        "self": { <br>
            "href": "https://test.api.amadeus.com/v1/shopping/activities/6378382", <br>
            "methods": [ <br>
                "GET" <br>
            ] <br>
        }, <br>
        "name": "A Night of White Truffles Supper Club", <br>
        "description": "Join the latest culinary trend of immersive and social dining where local hosts invite you into their homes for a magical meal.<br>The White Truffle, considered the diamond of the culinary world, was placed 4th on a list of the travel article _"25 things to eat before you die."_ Festin Supper Club presents dishes carefully created to compliment the distinct flavour of white truffles! This will be a fusion menu which will include many other flavors as well. *Dinners have different hosts at hand-picked, secret locations throughout city center.* And dishes vary by season - you can always ask for details of specific dates.", <br>
        "geoCode": { <br>
            "latitude": 52.51714834905604, <br>
            "longitude": 13.387748342746834 <br>
        }, <br>
        "price": { <br>
            "amount": "78.0", <br>
            "currencyCode": "EUR" <br>
        }, <br>
        "pictures": [ <br>
            Links of Pictures <br>
        ], <br>
        "bookingLink": "https://vizeater.co/events/30625?utm_source=amadeus-1381", <br>
        "minimumDuration": "3 hours" <br>
    }, <br>
<br><br>
But instead of this data we only get data like the following example (from Postman):<br>
    "data": [ <br>
        { <br>
            "type": "location", <br>
            "subType": "POINT_OF_INTEREST", <br>
            "id": "5A92DF1C00", <br>
            "self": { <br>
                "href": "https://test.api.amadeus.com/v1/reference-data/locations/pois/5A92DF1C00", <br>
                "methods": [ <br>
                    "GET" <br>
                ] <br>
            }, <br>
            "geoCode": { <br>
                "latitude": 52.51915, <br>
                "longitude": 13.401107 <br>
            }, <br>
            "name": "Berliner Dom", <br>
            "category": "SIGHTS", <br>
            "rank": 5, <br>
            "tags": [ <br>
                "church", <br>
                "sightseeing", <br>
                "temple", <br>
                "restaurant", <br>
                "tourguide", <br>
                "landmark", <br>
                "sights", <br>
                "attraction", <br>
                "commercialplace", <br>
                "activities", <br>
                "professionalservices" <br>
            ] <br>
        } <br>
<br>
Because of this we made the decision to use the tags instead of the description, generate pictures for each category and use this pictures instead of a picture of the links and don’t add the price and the booking link to the activity. Another problem of the API in Kotlin is that you don’t get the ranking of the activities, so we created a random rank to each item. It was also only possible to call 10 actives per location. The Service-API-Service has only access to a few location (Berlin and Paris are available).<br>
<br>
If we would publish the app, we would use the Enterprise API to have the best user experience.



## Future Enhancements

1. ### Collections:
   * Users can save their favorite locations and activities
   * Users can create their own collections

2. ### Travel Planning:
    * Users can create a travel plan and add locations to their route
    * Users can share their travel plan with friends

3. ### Social Integration:
   * Users can share their experiences with other users
   * Users can follow other users and see/share their collections

4. ### More Categories:
   * Users can explore more categories, such as hotels, bars or clubs