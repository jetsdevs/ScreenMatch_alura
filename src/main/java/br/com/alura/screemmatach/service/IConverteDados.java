package br.com.alura.screemmatach.service;

//Recebe o Json e transforma nos dados solicitado

public interface IConverteDados {
    <T> T obterDados(String json, Class<T> classe);
}
