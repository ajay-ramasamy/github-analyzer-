package com.github.analyzer.service;

import com.github.analyzer.entity.Issue;
import com.github.analyzer.entity.Repository;
import com.github.analyzer.exception.AppException;
import com.github.analyzer.repository.IssueRepository;
import com.github.analyzer.repository.RepositoryRepository;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiService {

    private final RepositoryRepository repositoryRepository;
    private final IssueRepository issueRepository;

    @Value("${openai.api.key}")
    private String openAiKey;

    public String generateRepoSummary(Long repoId) {
        Repository repo = repositoryRepository.findById(repoId)
                .orElseThrow(() -> new AppException("Repository not found", HttpStatus.NOT_FOUND));

        String prompt = String.format(
                "Generate a 2-3 sentence summary for a GitHub repository. Name: %s, Language: %s, Description: %s",
                repo.getName(), repo.getLanguage(), repo.getDescription());

        String summary = callOpenAi(prompt);
        repo.setAiSummary(summary);
        repositoryRepository.save(repo);
        return summary;
    }

    public String categorizeIssue(Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new AppException("Issue not found", HttpStatus.NOT_FOUND));

        String prompt = String.format(
                "Classify this GitHub issue into one of: Bug, Feature, Documentation, Enhancement, Security. " +
                "Reply with only the category. Issue title: \"%s\"", issue.getTitle());

        String category = callOpenAi(prompt).trim();
        issue.setLabel(category);
        issueRepository.save(issue);
        return category;
    }

    private String callOpenAi(String prompt) {
        OpenAiService service = new OpenAiService(openAiKey);
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(List.of(new ChatMessage("user", prompt)))
                .maxTokens(150)
                .build();
        return service.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();
    }
}
