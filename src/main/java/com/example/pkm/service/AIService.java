package com.example.pkm.service;

import java.util.List;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import com.example.pkm.repository.ContentItemRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AIService {
    private final ContentItemRepo contentItemRepo;

    private final OpenAiChatModel openAiChatModel;

    public String setAutoCategory(String description,String username) {
        List<String> categories = contentItemRepo.getAllCategoryList(username);

        String promptText = "From the given description of the content provided categorize what it implies in one word without a period(.)"
                            + description + "If the above content fits any of the already existing categories :" + categories + 
                            "then add it to that, else create a new Category name with Capitalized naming in ONE relevant word"+
                            "Keep it general, If its related to programming its programming, else nope, read the description carefully and kept professional"
                            +"You also need to be consistent with what give, even if the same description is repeated again you should give the same output which was once previously given";

                            
        var prompt = new Prompt(new UserMessage(promptText));

        ChatResponse response = openAiChatModel.call(prompt);
        String content = response.getResult().getOutput().getText();
        System.out.println("Category: " + content);
        return content;
    }

    public String setAutoTitle(String description){
        String promptText = "From the given description, generate an simple yet accurate, clear and concise title"
                            + description + "Make sure it matches with whatevere is being set here, be it a link, an article or whatever."+
                            "Just choose one which matches all the required conditions mentioned by me"
                            +"You also need to be consistent with what give, even if the same description is repeated again you should give the same output which was once previously given";


        var prompt = new Prompt(new UserMessage(promptText));
        ChatResponse response = openAiChatModel.call(prompt);
        String content = response.getResult().getOutput().getText();
        System.out.println("Title: " +content);
        return content;
    }

    public String setAutoType(String description){
        String promptText = "From the given description of the content provided assign it a 'type of content' without a period(.)"
                            + description + "It should be only of the following ContentType enums : ARTICLE, EMAIL, LINK, NOTE, MISCELLANEOUS" + 
                            "keep it VERY accurate and strict to what is given for example, if a given content is a link which contains an article, you are supposed to say link and not articles"+
                             "and only use the provided enums";

        var prompt = new Prompt(new UserMessage(promptText));
        ChatResponse response = openAiChatModel.call(prompt);
        String content = response.getResult().getOutput().getText();
        System.out.println("Type: " +content);
        return content;
    }

    public String generateSummary(String description){
        String promptText = "From the given description, be it an article, a note or any other form of content, generate a clear and concise summary" +
                            description;

        var prompt = new Prompt(new UserMessage(promptText));
        ChatResponse response = openAiChatModel.call(prompt);
        String content = response.getResult().getOutput().getText();
        System.out.println("Summary: " +content);
        return content;
    }
}
