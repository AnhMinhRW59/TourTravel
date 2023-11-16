package vti.travel.Service;

import org.springframework.data.domain.Page;
import vti.travel.Model.Entity.Booking;
import vti.travel.Model.Request.BookingRequest;
import vti.travel.Model.Request.SearchBookingRq;

import java.util.List;

public interface IBookingService {
    List<Booking> getAll();

    Booking getById(int bookingId);

    void createBooking(BookingRequest bookingRequest);

    Booking update(int bookingId, BookingRequest bookingRequest);

    void deleteBooking(int bookingId);

    List<Booking> findByFullName(String fullName);

    Page<Booking> search(SearchBookingRq searchBookingRq);
}
