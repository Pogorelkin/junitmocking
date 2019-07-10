package edu.epam.izhevsk.junit;

import org.junit.Before;
import static org.mockito.AdditionalMatchers.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PaymentControllerTest {

    @Mock
    AccountService mockAccountService;

    @Mock
    DepositService mockDepositService;

    @InjectMocks
    PaymentController paymentController = new PaymentController(mockAccountService, mockDepositService);

    @Before
    public void configureMocks() throws InsufficientFundsException {
        doReturn(true).when(mockAccountService).isUserAuthenticated(100L);
        doReturn(false).when(mockAccountService).isUserAuthenticated(not(eq(100L)));
        doReturn("Successful").when(mockDepositService).deposit(lt(100L), anyLong());
        doThrow(InsufficientFundsException.class).when(mockDepositService).deposit(geq(100L), anyLong());
    }

    @Test
    public void testDepositAmount50UserId100AuthOnceWithUserId100() throws InsufficientFundsException {
        paymentController.deposit(50L, 100L);
        verify(mockAccountService, times(1)).isUserAuthenticated(100L);
    }

    @Test(expected = SecurityException.class)
    public void testDepositForUnauthUser_SecurityExceptionExpected() throws InsufficientFundsException {
        paymentController.deposit(50L, 1L);
    }

    @Test(expected = InsufficientFundsException.class)
    public void testDepositOfLargeAmount_InsFundsExceptionExpected() throws InsufficientFundsException {
        paymentController.deposit(1000L, 100L);
    }
}
