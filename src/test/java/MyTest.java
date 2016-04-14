import com.jayway.restassured.RestAssured;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;


public class MyTest {

    private String apiKey = "7eac01dd-eff0-4c69-b0fc-0c043fabbd32";

    @Test
    public void test() {


        RestAssured.baseURI = "https://api.heroku.com";

        given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Authorization", "Bearer " + apiKey).
                when().
                get("/apps/test555656767").
        then().
                statusCode(200).
                and().
                assertThat().body("name", equalTo("test555656767") );


    }

    @Test
    public void createApp() {

        RestAssured.baseURI = "https://api.heroku.com";


        given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Content-Type", "application/json").
                header("Authorization", "Bearer " + apiKey).
                body("{\"name\" : \"test555656767\" }").
                when().
                post("/apps").
                then().
                statusCode(201).
        and().
                assertThat().body("name", equalTo("test555656767"));

    }

    @Test

    public void updateApp() {

        RestAssured.baseURI = "https://api.heroku.com";

        given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Content-Type", "application/json").
                header("Authorization", "Bearer " + apiKey).
                body("{\"name\" : \"test555656767\" }").
                when().
                patch("/apps/test555656767").
                then().
                statusCode(200).
                and().
                assertThat().body("updated_at", greaterThanOrEqualTo(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date())));
    }

    @Test (dependsOnMethods = { "updateApp" })

    public void deleteApp() {

        RestAssured.baseURI = "https://api.heroku.com";

        given().
                header("Accept", "application/vnd.heroku+json; version=3").
                header("Content-Type", "application/json").
                header("Authorization", "Bearer " + apiKey).
                body("{\"name\" : \"test555656767\" }").
                when().
                delete("/apps/test555656767").
                then().
                statusCode(200).
        and().
                assertThat().body("name", equalTo("test555656767"));;

    }
}
