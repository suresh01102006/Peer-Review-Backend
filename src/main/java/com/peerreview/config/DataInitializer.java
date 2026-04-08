package com.peerreview.config;

import com.peerreview.model.*;
import com.peerreview.model.enums.*;
import com.peerreview.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final ProjectRepository projectRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewAssignmentRepository reviewAssignmentRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded, skipping...");
            return;
        }
        log.info("Seeding demo data...");

        // Create users
        User admin = User.builder().name("Prof. Sharma").email("admin@demo.com")
                .password(passwordEncoder.encode("Admin@123")).role(Role.ADMIN)
                .bio("Computer Science Professor with 15 years of experience.")
                .skills("Java,Spring Boot,Machine Learning,Data Science").build();

        User alice = User.builder().name("Alice Johnson").email("alice@demo.com")
                .password(passwordEncoder.encode("Student@123")).role(Role.STUDENT)
                .bio("Full-stack developer passionate about React and Node.js.")
                .skills("React,JavaScript,Node.js,CSS,MongoDB").build();

        User bob = User.builder().name("Bob Smith").email("bob@demo.com")
                .password(passwordEncoder.encode("Student@123")).role(Role.STUDENT)
                .bio("Mobile app developer exploring cross-platform frameworks.")
                .skills("Flutter,Dart,Firebase,Android,iOS").build();

        User carol = User.builder().name("Carol Davis").email("carol@demo.com")
                .password(passwordEncoder.encode("Student@123")).role(Role.STUDENT)
                .bio("Data science enthusiast working with Python and ML models.")
                .skills("Python,TensorFlow,Pandas,Scikit-learn,SQL").build();

        User david = User.builder().name("David Kim").email("david@demo.com")
                .password(passwordEncoder.encode("Student@123")).role(Role.STUDENT)
                .bio("Backend engineer specializing in microservices architecture.")
                .skills("Java,Docker,Kubernetes,AWS,Spring Boot").build();

        userRepository.saveAll(Arrays.asList(admin, alice, bob, carol, david));

        // Create assignments
        Assignment webDev = Assignment.builder().title("Web Development Project")
                .description("Build a full-stack web application using any modern tech stack. Focus on UI/UX and functionality.")
                .deadline(LocalDateTime.now().plusDays(14)).category("Web Development").createdBy(admin).build();

        Assignment mlProject = Assignment.builder().title("Machine Learning Model")
                .description("Train and evaluate a machine learning model on a real-world dataset. Document your methodology.")
                .deadline(LocalDateTime.now().plusDays(21)).category("Data Science").createdBy(admin).build();

        assignmentRepository.saveAll(Arrays.asList(webDev, mlProject));

        // Create projects
        Project p1 = Project.builder().title("E-Commerce Platform")
                .description("A full-featured e-commerce platform built with React, Node.js and MongoDB. Features include product catalog, cart, payment integration, and admin panel.")
                .tags("React,Node.js,MongoDB,Express,Stripe").githubLink("https://github.com/alice/ecommerce")
                .submittedBy(alice).assignment(webDev).status(ProjectStatus.APPROVED).build();

        Project p2 = Project.builder().title("Task Management App")
                .description("Flutter mobile app for project management with real-time collaboration. Supports multiple workspaces and team chat.")
                .tags("Flutter,Firebase,Dart,Mobile").githubLink("https://github.com/bob/taskapp")
                .submittedBy(bob).assignment(webDev).status(ProjectStatus.APPROVED).build();

        Project p3 = Project.builder().title("Sentiment Analysis Dashboard")
                .description("Python-based ML pipeline for Twitter sentiment analysis with an interactive visualization dashboard.")
                .tags("Python,TensorFlow,NLP,Dashboard").githubLink("https://github.com/carol/sentiment")
                .submittedBy(carol).assignment(mlProject).status(ProjectStatus.APPROVED).build();

        Project p4 = Project.builder().title("Microservices Blog Platform")
                .description("Scalable blog platform using Spring Boot microservices deployed on Docker and Kubernetes.")
                .tags("Java,Spring Boot,Docker,Kubernetes,MySQL").githubLink("https://github.com/david/blog")
                .submittedBy(david).assignment(webDev).status(ProjectStatus.PENDING).build();

        Project p5 = Project.builder().title("AI Image Classifier")
                .description("Deep learning model using CNN to classify images into 100 categories with 94% accuracy.")
                .tags("Python,CNN,TensorFlow,Keras,Deep Learning").githubLink("https://github.com/carol/imgclassify")
                .submittedBy(carol).assignment(mlProject).status(ProjectStatus.APPROVED).build();

        projectRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));

        // Create reviews
        Review r1 = Review.builder().project(p1).reviewer(bob)
                .qualityRating(5).creativityRating(4).presentationRating(5)
                .comment("Outstanding project! The UI is very polished and the code architecture is clean. Payment integration works seamlessly. Would love to see more payment gateway options.")
                .anonymous(false).build();

        Review r2 = Review.builder().project(p1).reviewer(carol)
                .qualityRating(4).creativityRating(5).presentationRating(4)
                .comment("Really creative approach to the product filtering system. The real-time search is impressive. Minor improvements needed in mobile responsiveness.")
                .anonymous(false).build();

        Review r3 = Review.builder().project(p2).reviewer(alice)
                .qualityRating(4).creativityRating(4).presentationRating(5)
                .comment("Beautiful Flutter UI! The real-time collaboration feature is well implemented. Firebase integration is smooth. Could benefit from offline support.")
                .anonymous(false).build();

        Review r4 = Review.builder().project(p3).reviewer(david)
                .qualityRating(5).creativityRating(5).presentationRating(4)
                .comment("The ML pipeline is well-structured and the accuracy is impressive. The visualization dashboard makes insights easy to understand. Great documentation!")
                .anonymous(true).build();

        Review r5 = Review.builder().project(p5).reviewer(bob)
                .qualityRating(5).creativityRating(5).presentationRating(5)
                .comment("Exceptional work! 94% accuracy on ImageNet subset is impressive. The model architecture choices are well justified and the training process is well documented.")
                .anonymous(false).build();

        Review r6 = Review.builder().project(p2).reviewer(carol)
                .qualityRating(3).creativityRating(4).presentationRating(4)
                .comment("Good concept but some features feel incomplete. The team collaboration aspect needs more polish. Overall a solid foundation to build upon.")
                .anonymous(false).build();

        reviewRepository.saveAll(Arrays.asList(r1, r2, r3, r4, r5, r6));

        // Review Assignments
        ReviewAssignment ra1 = ReviewAssignment.builder().project(p4).reviewer(alice).completed(false).build();
        ReviewAssignment ra2 = ReviewAssignment.builder().project(p4).reviewer(bob).completed(false).build();
        reviewAssignmentRepository.saveAll(Arrays.asList(ra1, ra2));

        // Create a group conversation
        Conversation groupChat = Conversation.builder()
                .name("Web Dev Study Group").type(ConversationType.GROUP)
                .participants(Arrays.asList(alice, bob, carol, david)).build();
        conversationRepository.save(groupChat);

        Message m1 = Message.builder().conversation(groupChat).sender(alice)
                .content("Hey everyone! Has anyone started on the assignment yet?").build();
        Message m2 = Message.builder().conversation(groupChat).sender(bob)
                .content("Yes! I'm building a Flutter app. What about you?").build();
        Message m3 = Message.builder().conversation(groupChat).sender(carol)
                .content("Working on a sentiment analysis dashboard. Python and TensorFlow!").build();
        Message m4 = Message.builder().conversation(groupChat).sender(alice)
                .content("That sounds awesome! Could you share your preprocessing pipeline?").build();
        messageRepository.saveAll(Arrays.asList(m1, m2, m3, m4));

        // Notifications
        Notification n1 = Notification.builder().user(alice).type(NotificationType.REVIEW_RECEIVED)
                .message("Bob reviewed your project 'E-Commerce Platform'").read(false).build();
        Notification n2 = Notification.builder().user(alice).type(NotificationType.REVIEW_RECEIVED)
                .message("Carol reviewed your project 'E-Commerce Platform'").read(true).build();
        Notification n3 = Notification.builder().user(david).type(NotificationType.REVIEW_ASSIGNED)
                .message("You have been assigned to review: E-Commerce Platform").read(false).build();
        notificationRepository.saveAll(Arrays.asList(n1, n2, n3));

        log.info("✅ Demo data seeded successfully!");
        log.info("  Admin:   admin@demo.com / Admin@123");
        log.info("  Student: alice@demo.com / Student@123");
        log.info("  Student: bob@demo.com   / Student@123");
        log.info("  Student: carol@demo.com / Student@123");
        log.info("  Student: david@demo.com / Student@123");
    }
}
