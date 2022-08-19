package com.ramon.app.models;

import com.ramon.app.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;

public class Cuenta {
    private String persona;
    private BigDecimal saldo;
    private Banco banco;

    public Cuenta(String persona, BigDecimal saldo) {
        this.persona = persona;
        this.saldo = saldo;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public void debito(BigDecimal monto) throws DineroInsuficienteException{
        if(monto.doubleValue() > saldo.doubleValue())
            throw new DineroInsuficienteException("Dinero Insuficiente");

        saldo = saldo.subtract(monto);
    }

    public void credito(BigDecimal monto){
        saldo = saldo.add(monto);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cuenta cuenta = (Cuenta) o;

        if (persona != null ? !persona.equals(cuenta.persona) : cuenta.persona != null) return false;
        return saldo != null ? saldo.equals(cuenta.saldo) : cuenta.saldo == null;
    }

    @Override
    public int hashCode() {
        int result = persona != null ? persona.hashCode() : 0;
        result = 31 * result + (saldo != null ? saldo.hashCode() : 0);
        return result;
    }
}
