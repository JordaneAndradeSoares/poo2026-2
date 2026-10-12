package com.poo2026_2.model;

public class Celula{
   private int linha;
   private int coluna;
   private Professor professor;

   Celula(int linha, int coluna, Professor professor){
	this.linha=linha;
	this.coluna=coluna;
	this.professor=professor;
}

   public int getLinha(){
	return linha;
   }

   public void setLinha(int linha){
	this.linha=linha;
   }

   public int getColuna(){
	return coluna;
   }

   public void setColuna(int coluna){
	this.coluna=coluna;
   }

   public Professor getProfessor(){
	return professor;
   }

   public void setProfessor(Professor professor){
	this.professor=professor;
   }

   public boolean estaOcupada(){
	return this.professor != null;
   }
   //Verificar se tem um professor

   public void instalarProfessor(Professor professor){
   if (!estaOcupada()){
	   this.professor = professor;
	}
   
   }
   //Coloca o professor escolhido se estaOcupado for igual a false

   public void removerProfessor(){
      this.professor = null;
   }
   //Tira o professor que estiver aqui
}