package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DemoWebShopWishlistTests {

    private String email = "lowrencesammy@gmail.com";
    private String password = "Aa!11111";
    private String userCookie = null;
    private String quantityOfItemsInWishlist = null;

    @Test
    @DisplayName("Check number of items in wishlist")
    void checkQuantityOfProductsAddedToWishlist() {

        step("Get user authorization cookie", () -> {
            userCookie = given()
                    .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                    .formParam("Email", email)
                    .formParam("Password", password)
                    .when()
                    .post("http://demowebshop.tricentis.com/login")
                    .then()
                    .statusCode(302)
                    .extract().cookie("NOPCOMMERCE.AUTH");
        });

        step("Add item to wishlist", () -> {
            Response response = given()
                    .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                    .cookie("NOPCOMMERCE.AUTH", userCookie)
                    .body("addtocart_43.EnteredQuantity=1")
                    .when()
                    .post("http://demowebshop.tricentis.com/addproducttocart/details/43/2")
                    .then()
                    .statusCode(200)
                    .extract().response();
            quantityOfItemsInWishlist = response.path("updatetopwishlistsectionhtml");
        });

        step("Add cookie to browser", ()->{
            open("http://demowebshop.tricentis.com/favicon.ico");
            getWebDriver().manage().addCookie(
                    new Cookie("NOPCOMMERCE.AUTH", userCookie));
        });

        step("Check quantity of added items in wishlist", ()->{
            open("http://demowebshop.tricentis.com/");
            assertThat($(".wishlist-qty").text()).isEqualTo(quantityOfItemsInWishlist);
        });
    }
}
