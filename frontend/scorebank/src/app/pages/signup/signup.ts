import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LoadingComponent } from '../../shared/loading/loading'; 

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, LoadingComponent, FormsModule],
  templateUrl: './signup.html',
  styleUrls: ['./signup.css']
})
// Mudei o nome para SignupComponent para evitar erros no arquivo de rotas
export class SignupComponent implements OnInit {
goToSignin(): void {
    this.router.navigate(['/signin']);
  }

  // Controla a visibilidade do loading
  isLoading: boolean = false;
  
  // Controla quais frases aparecem
  currentPhrases: string[] = [];

  // Variável para saber em qual estágio estamos ('INTRO' ou 'ANALYSIS')
  loadingStage: 'INTRO' | 'ANALYSIS' = 'INTRO';

  // Dados do formulário
  formData = {
    name: '',
    cpf: '',
    age: null,
    income: null,
    profession: ''
  };

  constructor(private router: Router) {}

  ngOnInit(): void {
    // === LOADING DE ENTRADA (UX) ===
    this.loadingStage = 'INTRO';
    this.currentPhrases = ["Vamos criar sua conta..."];
    this.isLoading = true; 
  }

  // Chamado quando o usuário clica no botão "Solicitar Análise"
  onSubmit(): void {
    // Validação Simples
    if (!this.formData.name || !this.formData.cpf || !this.formData.age || !this.formData.income) {
      alert('Por favor, preencha todos os campos obrigatórios.');
      return;
    }

    console.log("Dados capturados:", this.formData);

    // === LOADING DE ANÁLISE ===
    this.loadingStage = 'ANALYSIS';
    this.currentPhrases = [
      "Consultando Serasa...", 
      "Calculando Risco...", 
      "Verificando Renda...",
      "Gerando Oferta..."
    ];
    this.isLoading = true; // Ativa o loading e esconde o formulário
  }

  // Chamado automaticamente quando o tempo do loading (2.5s) acaba
  onLoadingComplete(): void {
    this.isLoading = false; // Esconde o loading

    if (this.loadingStage === 'INTRO') {
      // Apenas libera o formulário para o usuário preencher
    } 
    else if (this.loadingStage === 'ANALYSIS') {
      // Simulação: Análise concluída, vai para o dashboard
      console.log("Análise finalizada! Redirecionando...");
      this.router.navigate(['/dashboard']);
    }
  }
}