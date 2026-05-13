package com.andersonfariasdev.contabancariaapi.domain.model.value;

import com.andersonfariasdev.contabancariaapi.domain.model.enums.TipoDocumento;
import com.andersonfariasdev.contabancariaapi.infrastructure.exception.ValidationException;

import java.util.Objects;

public final class Documento {

    private final String valor;
    private final TipoDocumento tipo;

    public Documento(String valor) {
        if (valor == null) throw new ValidationException("Documento não pode ser nulo");
        String clean = valor.replaceAll("\\D", "");
        if (clean.length() == 11) {
            this.tipo = TipoDocumento.CPF;
            if (!validaCPF(clean)) throw new ValidationException("CPF inválido");
        } else if (clean.length() == 14) {
            this.tipo = TipoDocumento.CNPJ;
            if (!validaCNPJ(clean)) throw new ValidationException("CNPJ inválido");
        } else {
            throw new ValidationException("Documento deve ser CPF(11) ou CNPJ(14)");
        }
        this.valor = clean;
    }

    public String getValor() {
        return valor;
    }

    public TipoDocumento getTipo() {
        return tipo;
    }

    private boolean validaCPF(String cpf) {
        // algoritmo simples de validação de CPF (sem máscara)
        if (cpf.chars().distinct().count() == 1) return false;
        try {
            int sum1 = 0;
            for (int i = 0; i < 9; i++) sum1 += (cpf.charAt(i) - '0') * (10 - i);
            int dv1 = 11 - (sum1 % 11);
            if (dv1 >= 10) dv1 = 0;
            int sum2 = 0;
            for (int i = 0; i < 10; i++) sum2 += (cpf.charAt(i) - '0') * (11 - i);
            int dv2 = 11 - (sum2 % 11);
            if (dv2 >= 10) dv2 = 0;
            return dv1 == (cpf.charAt(9) - '0') && dv2 == (cpf.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validaCNPJ(String cnpj) {
        // validação simplificada de CNPJ (sem máscara)
        if (cnpj.chars().distinct().count() == 1) return false;
        int[] t1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] t2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        try {
            int sum = 0;
            for (int i = 0; i < 12; i++) sum += (cnpj.charAt(i) - '0') * t1[i];
            int dv1 = sum % 11;
            dv1 = dv1 < 2 ? 0 : 11 - dv1;
            sum = 0;
            for (int i = 0; i < 13; i++) sum += (cnpj.charAt(i) - '0') * t2[i];
            int dv2 = sum % 11;
            dv2 = dv2 < 2 ? 0 : 11 - dv2;
            return dv1 == (cnpj.charAt(12) - '0') && dv2 == (cnpj.charAt(13) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Documento documento = (Documento) o;
        return Objects.equals(valor, documento.valor) && tipo == documento.tipo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor, tipo);
    }
}
