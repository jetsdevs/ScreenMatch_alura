package br.com.alura.screemmatach.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

public class ConsultaChatGpt {

    public static String obterTraducao(String texto){
        OpenAiService service = new OpenAiService("sk-proj-jawhnH2J3fB01F6jkDkwT3BlbkFJM16tt1YSk2g1Hljf8vmv");

        CompletionRequest requisicao = CompletionRequest.builder()
                .model("GPT-3.5")
                .prompt("Traduza para o portugues o texto: " +  texto)
                .maxTokens(1000)
                .temperature(0.7)
                .build();

        var resposta = service.createCompletion(requisicao);
        return resposta.getChoices().get(0).getText();
    }
}
