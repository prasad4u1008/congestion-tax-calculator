#Read Me First

##The following was discovered as part of building this project:

##Instructions 
How to build application:

  - Checkout the source code from github
  - cd to project folder
  - Execute: mvn clean install - to run build and run unit test
  - Execute: mvn spring-boot:run - to run the project as spring boot application
 
 How to trigger api call:
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