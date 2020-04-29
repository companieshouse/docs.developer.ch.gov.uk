package uk.gov.ch.developer.docs.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.developer.docs.models.user.IUserModel;
import uk.gov.ch.developer.docs.session.SessionService;
import uk.gov.companieshouse.session.Session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractPageControllerTest {

    @InjectMocks
    private ImplPageController controller;
    @Mock
    private IUserModel mockUserModel;
    @Mock
    private Session mockSession;
    @Mock
    private SessionService mockSessionService;

    @Test
    void getUser() {
        when(mockSessionService.getSessionFromContext()).thenReturn(mockSession);
        IUserModel out = controller.getUserModel();
        assertEquals(mockUserModel, out);
        verify(mockUserModel, times(1)).populateUserDetails(mockSession);
    }

    static class ImplPageController extends AbstractPageController {

        public ImplPageController() {
            super("title");
        }

        @Override
        public String getPath() {
            return null;
        }
    }
}