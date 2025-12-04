import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LoadingComponent } from '../../shared/loading/loading'; 
import { DataService } from '../../shared/loading/data.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule],
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
    fullName: '', // <--- MUDE DE 'name' PARA 'fullName'
    cpf: '',
    email: '',
    password: ''
  };

  constructor(private router: Router, private dataService: DataService) {}

  ngOnInit(): void {
    // === LOADING DE ENTRADA (UX) ===
    this.loadingStage = 'INTRO';
    this.currentPhrases = ["Vamos criar sua conta..."];
    this.isLoading = true; 
  }

  // Chamado quando o usuário clica no botão "Solicitar Análise"
  onSubmit(): void {
    if (!this.formData.fullName || !this.formData.cpf || !this.formData.email || !this.formData.password) {
      alert('Preencha todos os campos para continuar.');
      return;
    }

    console.log("Dados capturados:", this.formData);
    // Salva no Service
    this.dataService.setUserData(this.formData);

    

    // === LOADING DE ANÁLISE ===
    this.loadingStage = 'ANALYSIS';
    this.currentPhrases = [
      "Vamos completar seu cadastro..."
    ];
    this.isLoading = true; // Ativa o loading e esconde o formulário

    // Navega para a próxima etapa (Simulação)
    this.router.navigate(['/simulation']);
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