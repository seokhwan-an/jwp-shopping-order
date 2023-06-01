package cart.integration;

import cart.dao.CartItemDao;
import cart.dao.MemberDao;
import cart.dao.ProductDao;
import cart.domain.CartItem;
import cart.domain.Member;
import cart.dto.OrderRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

import static cart.fixture.MemberFixture.ako;
import static cart.fixture.ProductFixture.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class OrderIntegrationTest extends IntegrationTest {

    @Autowired
    private MemberDao memberDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private CartItemDao cartItemDao;

    private Member member;
    private List<Long> cartItemIds;

    @BeforeEach
    void setUp() {
        super.setUp();
        Long chickenId = productDao.createProduct(chicken);
        Long forkId = productDao.createProduct(fork);
        chicken = generateChicken(chickenId); // 9500 상품할인
        fork = generateFork(forkId); // 13500 멤버할인


        Long id = memberDao.addMember(ako);
        member = new Member(id, ako.getEmail(), ako.getPassword(), ako.getRank(), ako.getTotalPurchaseAmount());
        memberDao.updateMember(member);
        Long cartItem1 = cartItemDao.save(new CartItem(member, chicken));
        Long cartItem2 = cartItemDao.save(new CartItem(member, fork));

        cartItemIds = List.of(cartItem1, cartItem2);
    }

    @Test
    @DisplayName("올바른 주문을 생성한다.")
    void create_order() {
        // given
        OrderRequest orderRequest = new OrderRequest(
                cartItemIds,
                500,
                1500,
                25000,
                23000,
                3000,
                26000);

        // when
        ExtractableResponse<Response> result = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().preemptive().basic(member.getEmail(), member.getPassword())
                .body(orderRequest)
                .when()
                .post("/orders")
                .then()
                .log().all()
                .extract();

        // then
        String location = result.header("Location");
        assertThat(location).isNotBlank();
    }
}