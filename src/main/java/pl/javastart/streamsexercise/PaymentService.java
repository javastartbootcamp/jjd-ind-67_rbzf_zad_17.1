package pl.javastart.streamsexercise;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class PaymentService {

    private final PaymentRepository paymentRepository;
    private final DateTimeProvider dateTimeProvider;

    PaymentService(PaymentRepository paymentRepository, DateTimeProvider dateTimeProvider) {
        this.paymentRepository = paymentRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    /*
    Znajdź i zwróć płatności posortowane po dacie rosnąco
     */
    List<Payment> findPaymentsSortedByDateAsc() {
        return paymentRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Payment::getPaymentDate))
                .collect(Collectors.toList());
        //throw new RuntimeException("Not implemented");
    }

    /*
    Znajdź i zwróć płatności posortowane po dacie malejąco
     */
    List<Payment> findPaymentsSortedByDateDesc() {
        return paymentRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Payment::getPaymentDate).reversed())
                .collect(Collectors.toList());
        //throw new RuntimeException("Not implemented");
    }

    /*
    Znajdź i zwróć płatności posortowane po liczbie elementów rosnąco
     */
    List<Payment> findPaymentsSortedByItemCountAsc() {
        return paymentRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(p -> p.getPaymentItems().size()))
                .collect(Collectors.toList());

        //throw new RuntimeException("Not implemented");
    }

    /*
    Znajdź i zwróć płatności posortowane po liczbie elementów malejąco
     */
    List<Payment> findPaymentsSortedByItemCountDesc() {
        return paymentRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(p -> -p.getPaymentItems().size()))
                .collect(Collectors.toList());
        //throw new RuntimeException("Not implemented");
    }

    /*
    Znajdź i zwróć płatności dla wskazanego miesiąca
     */
    List<Payment> findPaymentsForGivenMonth(YearMonth yearMonth) {
        return paymentRepository.findAll()
                .stream()
                .filter(p -> YearMonth.of(p.getPaymentDate().getYear(), p.getPaymentDate().getMonth()).equals(yearMonth))
                .collect(Collectors.toList());
        //throw new RuntimeException("Not implemented");
    }

    /*
    Znajdź i zwróć płatności dla aktualnego miesiąca
     */
    List<Payment> findPaymentsForCurrentMonth() {
        return findPaymentsForGivenMonth(dateTimeProvider.yearMonthNow());
        //throw new RuntimeException("Not implemented");
    }

    /*
    Znajdź i zwróć płatności dla ostatnich X dni
     */
    List<Payment> findPaymentsForGivenLastDays(int days) {
        return paymentRepository.findAll()
                .stream()
                .filter(p -> (p.getPaymentDate().isAfter(dateTimeProvider.zonedDateTimeNow().minusDays(days))
                        && p.getPaymentDate().isBefore(dateTimeProvider.zonedDateTimeNow())))
                .collect(Collectors.toList());
        //throw new RuntimeException("Not implemented");
    }

    /*
    Znajdź i zwróć płatności z jednym elementem
     */
    Set<Payment> findPaymentsWithOnePaymentItem() {
        return paymentRepository.findAll()
                .stream()
                .filter(p -> (p.getPaymentItems().size() == 1))
                .collect(Collectors.toSet());
        //throw new RuntimeException("Not implemented");
    }

    /*
    Znajdź i zwróć nazwy produktów sprzedanych w aktualnym miesiącu
     */
    Set<String> findProductsSoldInCurrentMonth() {
        return findPaymentsForCurrentMonth()
                .stream()
                .map(Payment::getPaymentItems)
                .flatMap(List::stream)
                .map(PaymentItem::getName)
                .collect(Collectors.toSet());

        //throw new RuntimeException("Not implemented");
    }

    /*
    Policz i zwróć sumę sprzedaży dla wskazanego miesiąca
     */
    BigDecimal sumTotalForGivenMonth(YearMonth yearMonth) {
        return findPaymentsForGivenMonth(yearMonth)
                .stream()
                .map(Payment::getPaymentItems)
                .flatMap(List::stream)
                .map(PaymentItem::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //throw new RuntimeException("Not implemented");
    }

    /*
    Policz i zwróć sumę przyznanych rabatów dla wskazanego miesiąca
     */
    BigDecimal sumDiscountForGivenMonth(YearMonth yearMonth) {
        return findPaymentsForGivenMonth(yearMonth)
                .stream()
                .map(Payment::getPaymentItems)
                .flatMap(List::stream)
                .map(element -> element.getRegularPrice().subtract(element.getFinalPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //throw new RuntimeException("Not implemented");
    }

    /*
    Znajdź i zwróć płatności dla użytkownika z podanym mailem
     */
    List<PaymentItem> getPaymentsForUserWithEmail(String userEmail) {
        return paymentRepository.findAll()
                .stream()
                .filter(p -> p.getUser().getEmail().equals(userEmail))
                .map(Payment::getPaymentItems)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        //throw new RuntimeException("Not implemented");
    }

    /*
    Znajdź i zwróć płatności, których wartość przekracza wskazaną granicę
     */
    Set<Payment> findPaymentsWithValueOver(int value) {
        return paymentRepository.findAll()
                .stream()
                .filter((p -> getPaymentValue(p).compareTo(BigDecimal.valueOf(value)) > 0))
                .collect(Collectors.toSet());

        //throw new RuntimeException("Not implemented");
    }

    BigDecimal getPaymentValue(Payment p) {
        return p.getPaymentItems()
                .stream()
                .map(PaymentItem::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
