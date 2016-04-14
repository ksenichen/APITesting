import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static com.jayway.restassured.RestAssured.given;


public class MyTest {

    public static final String API_KEY = "7eac01dd-eff0-4c69-b0fc-0c043fabbd32";
    public static final String NAME = "test2api";
    public static final String RENAME = "a123456test";
    private Response response;
    private Response responseAppList;
    JSONObject api = new JSONObject();

    private Response getAllApplications() {

        return given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Authorization", "Bearer " + API_KEY).
                when().
                get("/apps");
    }

    @BeforeGroups({"create", "update", "delete"})
    public void setUp() throws Exception {
        RestAssured.baseURI = "https://api.heroku.com";
        api.put("name", NAME);
        response = given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Content-Type", "application/json").
                header("Authorization", "Bearer " + API_KEY).
                body(api.toJSONString()).
                when().
                post("/apps");
    }

    @AfterGroups("create")
    public void tearDown() throws Exception {
        response = given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Content-Type", "application/json").
                header("Authorization", "Bearer " + API_KEY).
                body(api.toJSONString()).
                when().
                delete("/apps/" + NAME);
    }

    @AfterGroups("update")
    public void tearDownUpdate() throws Exception {
        response = given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Content-Type", "application/json").
                header("Authorization", "Bearer " + API_KEY).
                body(api.toJSONString()).
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

    @Test(groups = "update")
    public void updateApp() {
        api.put("name", RENAME);
        response = given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Content-Type", "application/json").
                header("Authorization", "Bearer " + API_KEY).
                body(api.toJSONString()).
                when().
                patch("/apps/" + NAME).
                then().
                statusCode(200).
                contentType(ContentType.JSON).
                extract().
                response();
        System.out.println(response.asString());

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
                body(api.toJSONString()).
                when().
                delete("/apps/" + NAME).
                then().
                statusCode(200);
        responseAppList = getAllApplications();
        ArrayList<String> appList = responseAppList.path("name");
        Assert.assertFalse(appList.contains(NAME));
    }
}
