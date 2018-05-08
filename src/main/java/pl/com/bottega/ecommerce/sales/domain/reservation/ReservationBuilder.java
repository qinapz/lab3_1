package pl.com.bottega.ecommerce.sales.domain.reservation;

import java.util.Date;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation.ReservationStatus;

public class ReservationBuilder {

    private Reservation.ReservationStatus status = ReservationStatus.OPENED;
    private ClientData clientData = new ClientData(Id.generate(), "John Doe");
    private Date date = new Date();

    public ReservationBuilder() {}

    public ReservationBuilder withStatus(Reservation.ReservationStatus status) {
        this.status = status;
        return this;
    }

    public ReservationBuilder withClientData(ClientData clientData) {
        this.clientData = clientData;
        return this;
    }

    public ReservationBuilder withDate(Date date) {
        this.date = date;
        return this;
    }

    public Reservation build() {
        return new Reservation(Id.generate(), status, clientData, date);
    }

}
