package pl.com.bottega.ecommerce.sales.domain.invoicing;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.application.api.handler.AddProductCommandHandler;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;

@RunWith(MockitoJUnitRunner.class)

public class AddProductHandlerTest {

    private ReservationRepository mockedReservationRepository;
    private ProductRepository mockedProductRepository;
    private SuggestionService mockedSuggestionService;
    private ClientRepository mockedClientRepository;
    private AddProductCommand addProductCommand;
    private AddProductCommandHandler addProductCommandHandler;
    private Reservation reservation;
    private Product availableProduct, unavailableProduct;
    private ClientData clientData;
    private Client client;
    private Date date;

    @Before
    public void setUp() {
        mockedReservationRepository = mock(ReservationRepository.class);
        mockedProductRepository = mock(ProductRepository.class);
        mockedSuggestionService = mock(SuggestionService.class);
        mockedClientRepository = mock(ClientRepository.class);
        addProductCommandHandler = new AddProductCommandHandler();
        addProductCommand = new AddProductCommand(Id.generate(), Id.generate(), 1);
        client = new Client();
        date = new Date();
        clientData = new ClientData(Id.generate(), "John Doe");
        reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED, clientData, new Date());

        Whitebox.setInternalState(addProductCommandHandler, "reservationRepository", mockedReservationRepository);
        Whitebox.setInternalState(addProductCommandHandler, "productRepository", mockedProductRepository);
        Whitebox.setInternalState(addProductCommandHandler, "clientRepository", mockedClientRepository);

        when(mockedReservationRepository.load(addProductCommand.getOrderId())).thenReturn(reservation);
    }

    @Test
    public void handlerShouldInvokeLoadMethodOfReservationRepositoryOnce() {
        when(mockedProductRepository.load(Mockito.<Id> any())).thenReturn(availableProduct);
        addProductCommandHandler.handle(addProductCommand);
        verify(mockedReservationRepository, times(1)).load(Mockito.<Id> any());
    }

    @Test
    public void handlerShouldInvokeLoadMethodOfProductRepositoryOnce() {
        when(mockedProductRepository.load(Mockito.<Id> any())).thenReturn(availableProduct);
        addProductCommandHandler.handle(addProductCommand);
        verify(mockedProductRepository, times(1)).load(Mockito.<Id> any());
    }

    @Test
    public void handleForAvailableProductShouldNotInvokeLoadMethodOfSuggestionService() {
        when(mockedProductRepository.load(Mockito.<Id> any())).thenReturn(availableProduct);
        addProductCommandHandler.handle(addProductCommand);
        verify(mockedSuggestionService, times(0)).suggestEquivalent(Mockito.<Product> any(), Mockito.<Client> any());
    }

    @Test
    public void handleForNonAvailableProductShouldInvokeLoadMethodOfSuggestionServiceOnce() {
        when(mockedProductRepository.load(Mockito.<Id> any())).thenReturn(unavailableProduct);
        addProductCommandHandler.handle(addProductCommand);
        verify(mockedSuggestionService, times(1)).suggestEquivalent(Mockito.<Product> any(), Mockito.<Client> any());
    }

    @Test
    public void reservationShouldContainProductFromCommandAfterInvokingHandle() {
        when(mockedProductRepository.load(Mockito.<Id> any())).thenReturn(availableProduct);
        addProductCommandHandler.handle(addProductCommand);
        assertThat(reservation.contains(availableProduct), is(true));
    }

    @Test
    public void handleShouldInvokeSaveMethodOfReservationRepositoryOnce() {
        when(mockedProductRepository.load(Mockito.<Id> any())).thenReturn(availableProduct);
        addProductCommandHandler.handle(addProductCommand);
        verify(mockedReservationRepository, times(1)).save(Mockito.<Reservation> any());
    }

}
