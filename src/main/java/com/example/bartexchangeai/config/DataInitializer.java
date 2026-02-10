package com.example.bartexchangeai.config;

import com.example.bartexchangeai.model.user.Role;
import com.example.bartexchangeai.model.user.User;
import com.example.bartexchangeai.model.offer.Category;
import com.example.bartexchangeai.model.offer.Offer;
import com.example.bartexchangeai.model.exchange.Exchange;
import com.example.bartexchangeai.model.exchange.ExchangeStatus;
import com.example.bartexchangeai.model.exchange.Message;
import com.example.bartexchangeai.model.offer.OfferStatus;
import com.example.bartexchangeai.model.user.Review;
import com.example.bartexchangeai.model.group.Group;
import com.example.bartexchangeai.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Profile("dev")
public class DataInitializer {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final OfferRepository offerRepository;
    private final ExchangeRepository exchangeRepository;
    private final MessageRepository messageRepository;
    private final ReviewRepository reviewRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner initializeData() {
        return args -> {
            if (userRepository.count() == 0) {
                initSampleData();
            }
        };
    }

    private void initSampleData() {
        // Создаем пользователей
        User alice = new User();
        alice.setUsername("alice");
        alice.setEmail("alice@example.com");
        alice.setPassword(passwordEncoder.encode("password123"));
        alice.setRole(Role.USER);
        alice.setRating(4.5f);
        alice = userRepository.save(alice);

        User bob = new User();
        bob.setUsername("bob");
        bob.setEmail("bob@example.com");
        bob.setPassword(passwordEncoder.encode("password123"));
        bob.setRole(Role.USER);
        bob.setRating(4.2f);
        bob = userRepository.save(bob);

        User charlie = new User();
        charlie.setUsername("charlie");
        charlie.setEmail("charlie@example.com");
        charlie.setPassword(passwordEncoder.encode("password123"));
        charlie.setRole(Role.ADMIN);
        charlie.setRating(3.8f);
        charlie = userRepository.save(charlie);

        // Создаем категории
        Category electronics = new Category();
        electronics.setName("Electronics");
        electronics = categoryRepository.save(electronics);

        Category books = new Category();
        books.setName("Books");
        books = categoryRepository.save(books);

        Category sports = new Category();
        sports.setName("Sports");
        sports = categoryRepository.save(sports);

        Category home = new Category();
        home.setName("Home & Garden");
        home = categoryRepository.save(home);

        // Создаем группы
        Group techGroup = new Group();
        techGroup.setName("Tech Enthusiasts");
        techGroup.setDescription("Group for technology lovers");
        techGroup.setMembers(Set.of(alice, bob));
        techGroup = groupRepository.save(techGroup);

        Group bookClub = new Group();
        bookClub.setName("Book Club");
        bookClub.setDescription("Reading and book exchange community");
        bookClub.setMembers(Set.of(alice, charlie));
        bookClub = groupRepository.save(bookClub);

        // Создаем предложения
        Offer laptopOffer = new Offer();
        laptopOffer.setTitle("MacBook Pro 13-inch");
        laptopOffer.setDescription("Excellent condition MacBook Pro, 2022 model. Perfect for work or study.");
        laptopOffer.setStatus(OfferStatus.ACTIVE);
        laptopOffer.setUser(alice);
        laptopOffer.setCategory(electronics);
        laptopOffer.setGroup(techGroup);
        laptopOffer = offerRepository.save(laptopOffer);

        Offer phoneOffer = new Offer();
        phoneOffer.setTitle("iPhone 14");
        phoneOffer.setDescription("Mint condition iPhone 14, barely used. Comes with original box.");
        phoneOffer.setStatus(OfferStatus.ACTIVE);
        phoneOffer.setUser(bob);
        phoneOffer.setCategory(electronics);
        phoneOffer = offerRepository.save(phoneOffer);

        Offer bookOffer = new Offer();
        bookOffer.setTitle("Programming Books Collection");
        bookOffer.setDescription("Collection of programming books: Clean Code, Design Patterns, Algorithms.");
        bookOffer.setStatus(OfferStatus.ACTIVE);
        bookOffer.setUser(charlie);
        bookOffer.setCategory(books);
        bookOffer.setGroup(bookClub);
        bookOffer = offerRepository.save(bookOffer);

        Offer bikeOffer = new Offer();
        bikeOffer.setTitle("Mountain Bike");
        bikeOffer.setDescription("Great mountain bike, perfect for trails and city riding.");
        bikeOffer.setStatus(OfferStatus.ACTIVE);
        bikeOffer.setUser(alice);
        bikeOffer.setCategory(sports);
        bikeOffer = offerRepository.save(bikeOffer);

        Offer plantOffer = new Offer();
        plantOffer.setTitle("Indoor Plant Collection");
        plantOffer.setDescription("Beautiful collection of indoor plants. Great for home decoration.");
        plantOffer.setStatus(OfferStatus.COMPLETED);
        plantOffer.setUser(bob);
        plantOffer.setCategory(home);
        plantOffer = offerRepository.save(plantOffer);

        // Создаем обмены
        Exchange exchange1 = new Exchange();
        exchange1.setInitiator(bob);
        exchange1.setParticipant(alice);
        exchange1.setOffer(laptopOffer);
        exchange1.setStatus(ExchangeStatus.PENDING);
        exchange1.setDate(LocalDateTime.now().minusDays(2));
        exchange1 = exchangeRepository.save(exchange1);

        Exchange exchange2 = new Exchange();
        exchange2.setInitiator(charlie);
        exchange2.setParticipant(bob);
        exchange2.setOffer(phoneOffer);
        exchange2.setStatus(ExchangeStatus.COMPLETED);
        exchange2.setDate(LocalDateTime.now().minusDays(5));
        exchange2 = exchangeRepository.save(exchange2);

        // Создаем сообщения
        Message message1 = new Message();
        message1.setSender(bob);
        message1.setExchange(exchange1);
        message1.setContent("Hi Alice! I'm interested in your MacBook. Would you be interested in trading for my iPhone?");
        message1.setTimestamp(LocalDateTime.now().minusDays(2));
        messageRepository.save(message1);

        Message message2 = new Message();
        message2.setSender(alice);
        message2.setExchange(exchange1);
        message2.setContent("Hi Bob! That sounds interesting. Can you tell me more about the condition of your iPhone?");
        message2.setTimestamp(LocalDateTime.now().minusDays(2).plusHours(2));
        messageRepository.save(message2);

        Message message3 = new Message();
        message3.setSender(charlie);
        message3.setExchange(exchange2);
        message3.setContent("Thanks for the great trade! The phone works perfectly.");
        message3.setTimestamp(LocalDateTime.now().minusDays(4));
        messageRepository.save(message3);

        // Создаем отзывы
        Review review1 = new Review();
        review1.setReviewer(charlie);
        review1.setReviewedUser(bob);
        review1.setExchange(exchange2);
        review1.setRating(5);
        review1.setComment("Excellent trader! Phone was exactly as described and Bob was very responsive.");
        reviewRepository.save(review1);

        Review review2 = new Review();
        review2.setReviewer(bob);
        review2.setReviewedUser(charlie);
        review2.setExchange(exchange2);
        review2.setRating(4);
        review2.setComment("Good experience overall. Charlie was honest about the item condition.");
        reviewRepository.save(review2);

        System.out.println("Sample data initialized successfully!");
        System.out.println("Users: " + userRepository.count());
        System.out.println("Categories: " + categoryRepository.count());
        System.out.println("Offers: " + offerRepository.count());
        System.out.println("Exchanges: " + exchangeRepository.count());
        System.out.println("Messages: " + messageRepository.count());
        System.out.println("Reviews: " + reviewRepository.count());
        System.out.println("Groups: " + groupRepository.count());
    }
}
