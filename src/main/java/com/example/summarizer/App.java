package com.example.summarizer;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class App {

    private static final String END_MARKER = "END";

    public static void main(String[] args) throws IOException {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("""
                    ERROR: OPENAI_API_KEY is not set.

                    Export your API key first, e.g.:
                        export OPENAI_API_KEY="sk-..."

                    Optional environment variables:
                        OPENAI_MODEL     model name (default: gpt-4o-mini)
                        OPENAI_BASE_URL  custom endpoint for OpenAI-compatible APIs
                    """);
            System.exit(1);
        }

        OpenAiChatModel.OpenAiChatModelBuilder modelBuilder = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(System.getenv().getOrDefault("OPENAI_MODEL", "gpt-4o-mini"));
        String baseUrl = System.getenv("OPENAI_BASE_URL");
        if (baseUrl != null && !baseUrl.isBlank()) {
            modelBuilder.baseUrl(baseUrl);
        }

        ChatModel model = modelBuilder.build();
        TextSummarizer summarizer = AiServices.create(TextSummarizer.class, model);

        if (args.length > 0) {
            runFileMode(summarizer, args);
        } else {
            runInteractive(summarizer);
        }
    }

    private static void runFileMode(TextSummarizer summarizer, String[] args) throws IOException {
        Path path = Path.of(args[0]);
        if (!Files.isRegularFile(path)) {
            System.err.println("File not found: " + path);
            System.exit(1);
        }
        SummaryLength length = args.length > 1 ? SummaryLength.fromInput(args[1]) : null;
        if (length == null) {
            length = SummaryLength.MEDIUM;
        }
        String text = Files.readString(path);
        summarizeAndPrint(summarizer, text, length);
    }

    private static void runInteractive(TextSummarizer summarizer) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("=========================================");
        System.out.println("         AI TEXT SUMMARIZER              ");
        System.out.println("=========================================");
        System.out.println("Paste lengthy text and get a summary, keywords, and key takeaways.");

        while (true) {
            SummaryLength length = askLength(reader);
            if (length == null) {
                break;
            }

            System.out.println();
            System.out.println("Paste or type your text below.");
            System.out.println("When finished, enter " + END_MARKER + " on a new line:");
            String text = readText(reader);
            if (text.isBlank()) {
                System.out.println("No text provided. Try again.\n");
                continue;
            }

            summarizeAndPrint(summarizer, text, length);

            System.out.print("Summarize another text? [y/N]: ");
            String again = reader.readLine();
            if (again == null || !again.trim().equalsIgnoreCase("y")) {
                break;
            }
            System.out.println();
        }

        System.out.println("Goodbye!");
    }

    private static SummaryLength askLength(BufferedReader reader) throws IOException {
        while (true) {
            System.out.println();
            System.out.println("Choose summary length:");
            System.out.println("  1) Short     - 3-5 bullet points, core message only");
            System.out.println("  2) Medium    - 5-8 bullet points, main ideas + details");
            System.out.println("  3) Detailed  - 10+ bullet points, full coverage");
            System.out.println("  q) Quit");
            System.out.print("Selection [2]: ");

            String input = reader.readLine();
            if (input == null || input.trim().equalsIgnoreCase("q")) {
                return null;
            }
            if (input.isBlank()) {
                return SummaryLength.MEDIUM;
            }
            SummaryLength length = SummaryLength.fromInput(input);
            if (length != null) {
                return length;
            }
            System.out.println("Invalid choice. Enter 1, 2, 3 or q.");
        }
    }

    private static String readText(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().equals(END_MARKER)) {
                break;
            }
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

    private static void summarizeAndPrint(TextSummarizer summarizer, String text, SummaryLength length) {
        System.out.println();
        System.out.println("Summarizing (" + length.label() + ")...");

        SummaryReport report = summarizer.summarize(
                length.label(),
                length.promptGuidance(),
                text
        );

        System.out.println();
        System.out.println("================ SUMMARY ================");
        printBullets(report.getBulletPoints());
        System.out.println();
        System.out.println("================ KEYWORDS ===============");
        List<String> keywords = report.getKeywords();
        System.out.println(keywords == null || keywords.isEmpty()
                ? "(none)"
                : String.join("  |  ", keywords));
        System.out.println();
        System.out.println("============= KEY TAKEAWAYS =============");
        printBullets(report.getKeyTakeaways());
        System.out.println("=========================================");
        System.out.println();
    }

    private static void printBullets(List<String> items) {
        if (items == null || items.isEmpty()) {
            System.out.println("(none)");
            return;
        }
        for (String item : items) {
            System.out.println("  • " + item);
        }
    }
}
