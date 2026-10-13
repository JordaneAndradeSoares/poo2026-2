package com.poo2026_2.model;
import com.poo2026_2.model.utilizaveis.Cafeteira;

public class Celula{
   private int linha;
   private int coluna;
   private Entidade ocupante;

   Celula(int linha, int coluna, Entidade ocupante){
	this.linha=linha;
	this.coluna=coluna;
	
   if (ocupante != null && !tipoPermitido(ocupante)) {
   throw new IllegalArgumentException("A célula só pode armazenar professores ou cafeteiras.");
   }

   this.ocupante = ocupante;

   if (ocupante != null) {
      ocupante.setLinha(linha);
      ocupante.setColuna(coluna);
   }
}

   public int getLinha(){
	return linha;
   }

   public int getColuna(){
	return coluna;
   }

   public Entidade getOcupante() {
   return ocupante;
   }

   private void setOcupante(Entidade ocupante) {
      if (ocupante != null && !tipoPermitido(ocupante)) {
         throw new IllegalArgumentException("A célula só pode armazenar professores ou cafeteiras.");
      }

      this.ocupante = ocupante;

      if (ocupante != null) {
         ocupante.setLinha(linha);
         ocupante.setColuna(coluna);
      }
   }

   public Professor getProfessor() {
      if (ocupante instanceof Professor) {
         return (Professor) ocupante;
      }

      return null;
   }

   public boolean estaOcupada(){
	return ocupante != null;
   }
   //Verificar se tem um professor

   boolean instalar(Entidade entidade) {
      if (entidade == null || !tipoPermitido(entidade) || estaOcupada()) {
         return false;
      }

      setOcupante(entidade);
      return true;
   }

   public Entidade remover() {
      Entidade removido = ocupante;
      ocupante = null;
      return removido;
   }

   public void removerProfessor() {
      if (ocupante instanceof Professor) {
         ocupante = null;
      }
   }
   //Tira o professor que estiver aqui

   private boolean tipoPermitido(Entidade entidade) {
        return entidade instanceof Professor || entidade instanceof Cafeteira;
   }
}