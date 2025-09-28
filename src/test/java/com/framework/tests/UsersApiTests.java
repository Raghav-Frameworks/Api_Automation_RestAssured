package com.framework.tests;

import com.framework.core.ApiClient;
import com.framework.core.ResponseSpecFactory;
import com.framework.util.JsonUtils;
import com.framework.util.SchemaValidator;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class UsersApiTests {
    private final ApiClient api = new ApiClient();

    @Test(description = "GET list users with paging", groups = {"smoke"})
    public void listUsers() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", 2);
        Response resp = api.get("/users", null, queryParams, null);
        resp.then().spec(ResponseSpecFactory.ok());

        int page = resp.jsonPath().getInt("page");
        Assert.assertEquals(page, 2, "Page number should match");

        // Schema validation
        SchemaValidator.assertBodyMatchesSchema(resp, "schemas/reqres-list-users-schema.json");

        Allure.step("Verified schema and page attribute");
    }

    @Test(description = "POST create user", groups = {"regression"})
    public void createUser() {
        Map<String,Object> body = JsonUtils.readMap("/testdata/users/create-user.json");

        Response resp = api.post("/users", body, null, null, null);
        resp.then().spec(ResponseSpecFactory.created());

        String id = resp.jsonPath().getString("id");
        Assert.assertNotNull(id, "User ID should not be null");

        Allure.step("User created with id=" + id);
    }

    @Test(description = "PATCH update user", groups = {"regression"})
    public void patchUser() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "morpheus-updated");
        
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("id", 2);
        
        Response resp = api.patch("/users/{id}", body, pathParams, null, null);
        resp.then().spec(ResponseSpecFactory.ok());

        Assert.assertEquals(resp.jsonPath().getString("name"), "morpheus-updated");
    }

    @Test(description = "DELETE user", groups = {"regression"})
    public void deleteUser() {
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("id", 2);
        Response resp = api.delete("/users/{id}", pathParams, null, null);
        resp.then().statusCode(204);
    }
}
