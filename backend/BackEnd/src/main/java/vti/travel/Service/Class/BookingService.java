package vti.travel.Service.Class;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vti.travel.Model.Entity.Booking;
import vti.travel.Model.Entity.BookingStatus;
import vti.travel.Model.Entity.Tour;
import vti.travel.Model.Request.BaseRequest;
import vti.travel.Model.Request.BookingRequest;
import vti.travel.Model.Request.SearchBookingRq;
import vti.travel.Repository.BookingRepository;
import vti.travel.Repository.Specification.BookingTourSearch;
import vti.travel.Repository.TourRepository;
import vti.travel.Service.IBookingService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class BookingService implements IBookingService {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    TourRepository tourRepository;

    @Autowired
    TourService tourService;

    @Override
    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking getById(int bookingId) {
        Optional<Booking> optional = bookingRepository.findById(bookingId);

        return optional.orElse(null);
    }

    @Override
    public void createBooking(BookingRequest bookingRequest) {
        Tour tour = tourService.getTourById(bookingRequest.getTour());
        if (tour == null) {
            throw new RuntimeException("Không tìm thấy tour với ID: " + bookingRequest.getTour());
        }
        if (bookingRequest.getGuestSize() <= tour.getMaxGroupSize()) {
            Booking booking = new Booking();
            BeanUtils.copyProperties(bookingRequest, booking);
            booking.setAddress(bookingRequest.getAddress());
            booking.setTour(tour);
            booking.setTitleTour(tour.getTitle());
            booking.setEmail(bookingRequest.getEmail());
            booking.setFullName(bookingRequest.getFullName());
            booking.setNote(bookingRequest.getNote());
            booking.setPhoneNumber(bookingRequest.getPhoneNumber());
            booking.setGuestSize(bookingRequest.getGuestSize());
            booking.setBookingAt(LocalDate.now());
            booking.setStatus(BookingStatus.PENDING);
            bookingRepository.save(booking);
            // Cập nhật lại maxGroupSize của tour
            int updatedMaxGroupSize = tour.getMaxGroupSize() - bookingRequest.getGuestSize();
            tour.setMaxGroupSize(updatedMaxGroupSize);
            tourRepository.save(tour);
        } else {
            throw new RuntimeException("Không thể đặt tour vì số lượng booking đã vượt quá số lượng slot trống.");
        }
    }


    @Override
    public Booking update(int bookingId, BookingRequest bookingRequest) {


        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking existingBooking = optionalBooking.get();
            Tour tour = tourService.getTourById(bookingRequest.getTour());

            if (tour != null) {
                BeanUtils.copyProperties(bookingRequest, existingBooking);
                existingBooking.setTitleTour(tour.getTitle());
                existingBooking.setAddress(bookingRequest.getAddress());
                existingBooking.setEmail(bookingRequest.getEmail());
                existingBooking.setFullName(bookingRequest.getFullName());
                existingBooking.setNote(bookingRequest.getNote());
                existingBooking.setGuestSize(bookingRequest.getGuestSize());
                existingBooking.setPhoneNumber(bookingRequest.getPhoneNumber());
                existingBooking.setTotalAmount(bookingRequest.getTotalAmount());
                existingBooking.setStatus(bookingRequest.getStatus());
                existingBooking.setTour(tour);
                bookingRepository.save(existingBooking);
                if (BookingStatus.FAILED.equals(bookingRequest.getStatus())){
                    tour.setMaxGroupSize(tour.getMaxGroupSize()+ existingBooking.getGuestSize());
                    tourRepository.save(tour);
                }
                return existingBooking;
            } else {
                throw new RuntimeException("Không tìm thấy tour với ID: " + bookingRequest.getTour());
            }
        } else {
            throw new RuntimeException("Không tìm thấy booking với ID: " + bookingId);
        }
    }


    @Override
    public void deleteBooking(int bookingId) {
        Optional<Booking> optional = bookingRepository.findById(bookingId);
        if (optional.isPresent()) {
            bookingRepository.deleteById(bookingId);
        } else {
            throw new RuntimeException("Booking bạn đang muốn xóa không tồn tại");
        }
    }

    @Override
    public List<Booking> findByFullName(String fullName) {
        List<Booking> bookings = bookingRepository.findBookingByFullName(fullName);
        if (bookings != null) {
            return bookings;
        }
        return null;
    }

    @Override
    public Page<Booking> search(SearchBookingRq searchBookingRq) {
        Specification<Booking> specification = BookingTourSearch.buildCondition(searchBookingRq);
        PageRequest pageRequest = BaseRequest.buildPageRequest(searchBookingRq);
        return bookingRepository.findAll(specification, pageRequest);
    }
}
