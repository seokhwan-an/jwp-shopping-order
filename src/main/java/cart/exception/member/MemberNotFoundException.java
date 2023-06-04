package cart.exception.member;

public class MemberNotFoundException extends MemberException{

    private static final String MEMBER_NOT_FOUND_EXCEPTION_MESSAGE = "해당 사용자는 존재하지 않습니다.";

    public MemberNotFoundException() {
        super(MEMBER_NOT_FOUND_EXCEPTION_MESSAGE);
    }
}