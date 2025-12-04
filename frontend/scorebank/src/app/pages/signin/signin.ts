import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-signin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './signin.html',
  styleUrls: ['./signin.css']
})
export class SigninComponent {

  error: string = '';

  formData = {
    email: '',
    password: ''
  };

  constructor(
    private router: Router,
    private auth: AuthService
  ) {}

  goToSignup(): void {
    this.router.navigate(['/signup']);
  }

  onSubmit(): void {
    this.error = '';

    // Validação simples
    if (!this.formData.email || !this.formData.password) {
      this.error = "Preencha todos os campos.";
      return;
    }

    // Chamada ao backend
    this.auth.login(this.formData).subscribe({
      next: (response) => {
        // Salvar dados do usuário no localStorage
        localStorage.setItem('userId', response.id.toString());
        localStorage.setItem('userName', response.fullNome);
        localStorage.setItem('userEmail', response.email);

        // Redirecionar para o dashboard
        setTimeout(() => {
          console.log('Redirecionando...');
          this.router.navigate(['/simulation']);
        }, 1500);
      },
      error: (err) => {
        if (err.status === 401) {
          this.error = "Email ou senha incorretos.";
        } else if (err.status === 404) {
          this.error = "Usuário não encontrado.";
        } else {
          this.error = "Erro ao fazer login. Tente novamente.";
        }
      }
    });
  }
}