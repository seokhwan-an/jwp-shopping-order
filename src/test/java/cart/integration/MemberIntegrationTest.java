package cart.integration;

import cart.dao.MemberDao;
import cart.domain.Member;
import cart.dto.MemberResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static cart.fixture.MemberFixture.ako;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Sql(scripts = "/truncate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class MemberIntegrationTest extends IntegrationTest {

    @Autowired
    private MemberDao memberDao;

    private Member member;

    @BeforeEach
    void setUp() {
        super.setUp();
        Long id = memberDao.addMember(ako);
        member = new Member(id, ako.getEmail(), ako.getPassword(), ako.getRank(), ako.getTotalPurchaseAmount());
        memberDao.updateMember(member);
    }

    @Test
    @DisplayName("멤버를 조회한다.")
    void find_member() {
        // given
        MemberResponse expect = new MemberResponse(member);

        // when
        ExtractableResponse<Response> result = given().log().all()
                .auth().preemptive().basic(member.getEmail(), member.getPassword())
                .when()
                .get("/member")
                .then().log().all()
                .extract();

        // then
        MemberResponse respone = result.jsonPath().getObject(".", MemberResponse.class);
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(respone).usingRecursiveComparison().isEqualTo(expect);
    }

}
