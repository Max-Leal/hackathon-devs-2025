import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { DataService } from '../../shared/loading/data.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './signup.html',
  styleUrls: ['./signup.css']
})
export class SignupComponent implements OnInit {

  isLoading: boolean = false;
  currentPhrases: string[] = [];
  loadingStage: 'INTRO' | 'ANALYSIS' = 'INTRO';

  error: string = '';
  success: string = '';

  formData = {
    fullName: '',
    cpf: '',
    email: '',
    password: '',
    age: ''
  };

  constructor(
    private router: Router,
    private auth: AuthService,
    private dataService: DataService
  ) {}

  ngOnInit(): void {
    // CORREÇÃO: Não iniciar com loading ativo
    this.isLoading = false;
  }

  goToSignin(): void {
    this.router.navigate(['/signin']);
  }

  onSubmit(): void {
    this.error = '';
    this.success = '';

    // Validação simples
    if (!this.formData.fullName || !this.formData.cpf || !this.formData.age || 
        !this.formData.email || !this.formData.password) {
      this.error = "Preencha todos os campos.";
      return;
    }

    // Chamada ao backend
    this.auth.register(this.formData).subscribe({
      next: (response) => {
        console.log('Resposta do backend:', response);
        console.log('Status: 200 - Sucesso!');
        
        this.success = "Conta criada com sucesso!";
        console.log('Variável success definida:', this.success);

        // Redirecionar após 2 segundos
        setTimeout(() => {
          console.log('Redirecionando...');
          this.router.navigate(['/signin']);
        }, 2000);
      },
      error: (err) => {
        console.error('Erro no registro:', err);

        if (err.status === 400) {
          this.error = err.error?.message || "CPF, email ou dados inválidos.";
        } else if (err.status === 409) {
          this.error = "Usuário já cadastrado.";
        } else {
          this.error = "Erro ao criar conta. Tente novamente.";
        }
      }
    });
  }

  onLoadingComplete(): void {
    this.isLoading = false;
  }
}