package vti.travel.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.travel.Model.Entity.Booking;
import vti.travel.Model.Request.BookingRequest;
import vti.travel.Model.Request.SearchBookingRq;
import vti.travel.Service.Class.BookingService;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // chức năng hiện bookings
    @GetMapping("get-all")
    public ResponseEntity<List<Booking>> getAllBookings() {

        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getAll());
    }

    // chức năng tìm kiếm Booking theo id
    @GetMapping("/get/{bookingId}")
    public ResponseEntity<Booking> getBookingById(@PathVariable int bookingId) {
        Booking booking = bookingService.getById(bookingId);
        if (booking != null) {
            return ResponseEntity.ok(booking);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // tạo mới booking
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest bookingRequest) {
        bookingService.createBooking(bookingRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // update booking
    @PutMapping("/update/{bookingId}")
    public ResponseEntity<Booking> updateBooking(@PathVariable int bookingId, @RequestBody BookingRequest bookingRequest) {
        try {
            Booking updatedBooking = bookingService.update(bookingId, bookingRequest);
            return ResponseEntity.ok(updatedBooking);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // xóa booking
    @DeleteMapping("/delete/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable int bookingId) {
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    // tìm kiếm theo tên booking
    @PostMapping("/fullname")
    public ResponseEntity<List<Booking>> findByFullName(@RequestBody String fullName) {
        List<Booking> bookings = bookingService.findByFullName(fullName);
        if (bookings != null) {
            return ResponseEntity.ok(bookings);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping("/search")
    public Page<Booking> search(@RequestBody SearchBookingRq searchBookingRq) {
        return bookingService.search(searchBookingRq);
    }
}
