package com.madhuka.tutor_finder_backend.controller;

import com.madhuka.tutor_finder_backend.dto.ChatRequest;
import com.madhuka.tutor_finder_backend.entity.TutorProfile;
import com.madhuka.tutor_finder_backend.repository.TutorProfileRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/chatbot")
public class PublicChatbotController {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody ChatRequest request) {
        List<Message> springMessages = new ArrayList<>();

        // 1. Fetch approved tutors from database
        List<TutorProfile> approvedTutors = tutorProfileRepository.findByApprovalStatus("APPROVED");

        // 2. Format tutors into system context
        String tutorsContext = approvedTutors.stream()
                .map(tutor -> String.format(
                        "- Name: %s\n  Location: %s\n  Bio: %s\n  Subjects: %s\n  Phone: %s",
                        tutor.getFullName(),
                        tutor.getLocation(),
                        tutor.getBio() != null ? tutor.getBio() : "N/A",
                        tutor.getSubjects() != null ? tutor.getSubjects().stream().map(s -> s.getName()).collect(Collectors.joining(", ")) : "N/A",
                        tutor.getPhoneNumber() != null ? tutor.getPhoneNumber() : "N/A"
                ))
                .collect(Collectors.joining("\n\n"));

        // 3. Build system instruction prompt
        String systemInstruction = "You are the Tutor Finder AI Assistant, a friendly and helpful helper on our tutor booking platform.\n"
                + "Your primary goal is to help students find and connect with matching tutors based on subject, name, location, or background.\n\n"
                + "Here is the list of active, approved tutors currently on our platform:\n\n"
                + tutorsContext + "\n\n"
                + "Rules:\n"
                + "1. ALWAYS suggest specific tutors from the list above when they match the user's requirements.\n"
                + "2. Mention the tutor's name, subjects they teach, location, and key points from their bio.\n"
                + "3. Be concise, friendly, and helpful. Keep responses relatively short (under 3 paragraphs).\n"
                + "4. If no tutors match, explain that politely and suggest other relevant subjects or ask them to clarify what they are looking for.\n"
                + "5. Do NOT answer general knowledge questions that are completely unrelated to education, tutoring, or tutor finder app (e.g. 'tell me a recipe for cookies' or 'who is the president of France'). Politely decline and guide the user back to finding tutors.";

        springMessages.add(new SystemMessage(systemInstruction));

        // 4. Populate chat history
        if (request.getMessages() != null) {
            for (var msg : request.getMessages()) {
                if ("user".equalsIgnoreCase(msg.getRole())) {
                    springMessages.add(new UserMessage(msg.getContent()));
                } else if ("assistant".equalsIgnoreCase(msg.getRole()) || "model".equalsIgnoreCase(msg.getRole())) {
                    springMessages.add(new AssistantMessage(msg.getContent()));
                }
            }
        }

        // 5. Query Gemini model
        Prompt prompt = new Prompt(springMessages);
        var response = chatModel.call(prompt);
        String assistantResponse = response.getResult().getOutput().getText();

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("response", assistantResponse);

        return ResponseEntity.ok(responseBody);
    }
}
