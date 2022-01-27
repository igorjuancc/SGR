package br.com.sgr.facade;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BalancoFacade {

    public static List<Integer> listaAnoBalanco() {
        List<Integer> listaRtn;
        List<Integer> listaAno = new ArrayList<>();
        Integer primeiroAno = LogBDCadastroMoradorFacade.anoPrimeiroMoradorCadastro(0).intValue();

        if (primeiroAno > 0) {
            Calendar hoje = Calendar.getInstance();
            for (int i = primeiroAno; i <= hoje.get(Calendar.YEAR); i++) {
                listaAno.add(i);
            }
        }
        listaAno.addAll(FinanceiroFacade.listaAnoRegistro(1));
        listaRtn = listaAno.stream().distinct().collect(Collectors.toList());
        Collections.sort(listaRtn);
        Collections.reverse(listaRtn);
        return listaRtn;
    }
}
