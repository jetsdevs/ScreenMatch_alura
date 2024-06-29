package br.com.alura.screemmatach;

import br.com.alura.screemmatach.principal.Principal;
import br.com.alura.screemmatach.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreemmatachApplication implements CommandLineRunner {
	@Autowired
	private SerieRepository repositorio;

	public static void main(String[] args) {
		SpringApplication.run(ScreemmatachApplication.class, args);

	}
	@Override
	public void run(String... args) throws Exception{
		Principal principal = new Principal(repositorio);
		principal.exibeMenu();

	}

}
