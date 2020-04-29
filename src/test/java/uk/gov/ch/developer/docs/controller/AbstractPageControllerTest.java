package uk.gov.ch.developer.docs.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.developer.docs.models.user.IUserModel;
import uk.gov.ch.oauth.session.SessionFactory;
import uk.gov.companieshouse.session.Session;

@ExtendWith(MockitoExtension.class)
class AbstractPageControllerTest {

    @InjectMocks
    private ImplPageController controller;
    @Mock
    private IUserModel mockUserModel;
    @Mock
    private Session mockSession;
    @Mock
    private SessionFactory mockSessionFactory;

    @Test
    void getUser() {
        when(mockSessionFactory.getSessionFromContext()).thenReturn(mockSession);
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