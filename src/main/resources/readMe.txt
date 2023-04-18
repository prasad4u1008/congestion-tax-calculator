README 

Instructions 
- How to build application:
  - Checkout the source code from github
  - cd to project folder
  - Execute: mvn clean install - to run build and run unit test
  - Execute: mvn spring-boot:run - to run the project as spring boot application

- How to trigger api call:
  - Send POST request to:
    - Endpoint: /api/computeTax
    - Request param: city - (String) configuration city file
    - Request body: Json data
      - vehicle - (String) vehicle type
      - dates - (Array) list of entries
    - Request headers:
      - Content-Type: application/json
      - Accept: application/json

    - Response: Json data
      - vehicle - (String) vehicle type
      - taxAmount - (int) amount of calculated tax

  - Sample below

    HTTP Method = POST
    Request URI = /api/computeTax
    Parameters = {city=[Gothenburg]}
    Headers = [Content-Type:"application/json;charset=UTF-8", Accept:"application/json", Content-Length:"71"]
    Body = {"vehicle":"Car","dates":["2013-01-14 06:01:01","2013-01-14 06:58:01"]}


Issues Identified by provided code
  - IsTollFreeDate() method has a bug in finding toll-free dates
    - missing days: 20 May, 31 Oct, 30 Dec
    - logical issue check at year == 2013 because Date.getYear is returning the difference between current year and 1900, thus the expected value is 113
  - GetTollFee() method has the bug in finding toll fee
    - condition is wrong for the slot from 08:30-14:59, 15:30-16:59

Solution:
  - tollFreeVehicles is loaded as the configuration from properties file    
  - IsTollFreeDate() method is refactored to segregate the logic of public holiday is toll free,the previous date before holiday is toll free, the holiday fall in Saturday or Sunday
  - GetTollFee() method is refactored to support the external configuration for the hours and amounts for congestion tax rules
  - Change the deprecated Date api implementation with Calendar api 
  
Open Points:
  - Based on the given scribbled dates in front of the assignment, the program will take the series of input date for which the congestion tax had to be calculated
 
  - Extension / Bonus points:
    - City tax config is designed as external standard properties file. This will allow the external editor to change the config file during runtime and the new rule is applied into the system.
    - Further more, the introduction of new city tax config in the system is also straight forward, only need to add the new properties file, the existing rule should be auto supported for new city.

Data Structures used
  - Config object CityTaxConfiguration is designed with below attributes :
   - private String cityName;
   - private int maxAmountPerDay;
   - private int singleChargeInMinutes;
   - private Set<String> tollFreeVehicles = new HashSet<>();
   - private Map<String, Integer> tollAmount = new HashMap<>();
   - private Set<String> publicHolidays = new HashSet<>();
   - private Set<Integer> freeMonths = new HashSet<>();
- These fields would allow us to store the data in the appropriate format and also eliminate all the duplicated data

The following was discovered as part of building this project:

* The package name 'com.volvo.test.congestiontax' .

