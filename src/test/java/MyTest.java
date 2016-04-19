import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.ArrayList;

import static com.jayway.restassured.RestAssured.given;


public class MyTest {

    public static final String API_KEY = "7eac01dd-eff0-4c69-b0fc-0c043fabbd32";
    public static final String NAME = "test2api";
    public static final String RENAME = "a123456test";
    public static final String SHORT_NAME = "a";
    public static final String LONG_NAME = "q123456789012345678901234567890";
    public static final String UPPER_CASE = "qwertY";
    public static final String NAME_STARTS_DIGIT = "1qwerty";
    private Response response;
    private Response responseAppList;
    JSONObject app = new JSONObject();

    private Response getAllApplications() {

        return given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Authorization", "Bearer " + API_KEY).
                when().
                get("/apps");
    }

    @BeforeClass
    public void setUpBaseUri() throws Exception {
        RestAssured.baseURI = "https://api.heroku.com";
    }

    @BeforeGroups({"create", "read", "delete"})
    public void setUp() throws Exception {

        app.put("name", NAME);
        response = given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Content-Type", "application/json").
                header("Authorization", "Bearer " + API_KEY).
                body(app.toJSONString()).
                when().
                post("/apps");
    }

    @BeforeGroups("update")
    public void setUpUpdate() throws Exception {
        app.put("name", NAME);
        response = given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Content-Type", "application/json").
                header("Authorization", "Bearer " + API_KEY).
                body(app.toJSONString()).
                when().
                post("/apps");
        app.put("name", RENAME);
    }

    @AfterGroups({"create", "read"})
    public void tearDown() throws Exception {
        response = given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Content-Type", "application/json").
                header("Authorization", "Bearer " + API_KEY).
                body(app.toJSONString()).
                when().
                delete("/apps/" + NAME);
    }

    @AfterGroups("update")
    public void tearDownUpdate() throws Exception {
        response = given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Content-Type", "application/json").
                header("Authorization", "Bearer " + API_KEY).
                body(app.toJSONString()).
                when().
                delete("/apps/" + RENAME);
    }


    @Test(groups = "create")
    public void createApp() {
        response.
                then().
                statusCode(201);
        responseAppList = getAllApplications();
        ArrayList<String> appList = responseAppList.path("name");
        Assert.assertTrue(appList.contains(NAME));
    }

    @Test(groups = "read")
    public void readApp() {
        given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Authorization", "Bearer " + API_KEY).
                when().
                get("/apps/" + NAME).
                then().
                statusCode(200);
    }

    @Test(groups = "update")
    public void updateApp() {
        response = given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Content-Type", "application/json").
                header("Authorization", "Bearer " + API_KEY).
                body(app.toJSONString()).
                when().
                patch("/apps/" + NAME).
                then().
                statusCode(200).
                contentType(ContentType.JSON).
                extract().
                response();
        responseAppList = getAllApplications();
        ArrayList<String> appList = responseAppList.path("name");
        Assert.assertTrue(appList.contains(RENAME));

    }

    @Test(groups = "delete")
    public void deleteApp() {
        given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Content-Type", "application/json").
                header("Authorization", "Bearer " + API_KEY).
                body(app.toJSONString()).
                when().
                delete("/apps/" + NAME).
                then().
                statusCode(200);
        responseAppList = getAllApplications();
        ArrayList<String> appList = responseAppList.path("name");
        Assert.assertFalse(appList.contains(NAME));
    }

    @Test(groups = "negative")
    public void readNonExistingApp() {
        app.put("name", NAME);
        given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Authorization", "Bearer " + API_KEY).
                when().
                get("/apps" + NAME).
                then().
                statusCode(404);
    }

    @Test(groups = "negative")
    public void updateNonExistingApp() {
        app.put("name", RENAME);
        given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Authorization", "Bearer " + API_KEY).
                body(app.toJSONString()).
                when().
                patch("/apps" + NAME).
                then().
                statusCode(404);
    }

    @Test(groups = "negative")
    public void deleteNonExistingApp() {
        app.put("name", NAME);
        given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Authorization", "Bearer " + API_KEY).
                body(app.toJSONString()).
                when().
                delete("/apps" + NAME).
                then().
                statusCode(404);
    }

    @Test(groups = "create")
    public void createWithSameName() {
        given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Content-Type", "application/json").
                header("Authorization", "Bearer " + API_KEY).
                body(app.toJSONString()).
                when().
                post("/apps").
                then().
                statusCode(422);
    }

    @Test(groups = "negative")
    public void createWithShortName(){
        app.put("name", SHORT_NAME);
        given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Content-Type", "application/json").
                header("Authorization", "Bearer " + API_KEY).
                body(app.toJSONString()).
                when().
                post("/apps").
                then().
                statusCode(422);
        responseAppList = getAllApplications();
        ArrayList<String> appList = responseAppList.path("name");
        Assert.assertFalse(appList.contains(SHORT_NAME));
    }

    @Test(groups = "negative")
    public void createWithLongName(){
        app.put("name", LONG_NAME);
        given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Content-Type", "application/json").
                header("Authorization", "Bearer " + API_KEY).
                body(app.toJSONString()).
                when().
                post("/apps").
                then().
                statusCode(422);
        responseAppList = getAllApplications();
        ArrayList<String> appList = responseAppList.path("name");
        Assert.assertFalse(appList.contains(LONG_NAME));
    }

    @Test(groups = "negative")
    public void createWithUpperCaseName(){
        app.put("name", UPPER_CASE);
        given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Content-Type", "application/json").
                header("Authorization", "Bearer " + API_KEY).
                body(app.toJSONString()).
                when().
                post("/apps").
                then().
                statusCode(422);
        responseAppList = getAllApplications();
        ArrayList<String> appList = responseAppList.path("name");
        Assert.assertFalse(appList.contains(UPPER_CASE));
    }

    @Test(groups = "negative")
    public void createNameStartsWithDigit(){
        app.put("name", NAME_STARTS_DIGIT);
        given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Content-Type", "application/json").
                header("Authorization", "Bearer " + API_KEY).
                body(app.toJSONString()).
                when().
                post("/apps").
                then().
                statusCode(422);
        responseAppList = getAllApplications();
        ArrayList<String> appList = responseAppList.path("name");
        Assert.assertFalse(appList.contains(NAME_STARTS_DIGIT));
    }


}
