import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DataService } from '../../shared/loading/data.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './signup.html',
  styleUrls: ['./signup.css']
})
export class SignupComponent implements OnInit {
  isLoading = false;
  currentPhrases: string[] = [];
  loadingStage: 'INTRO' | 'ANALYSIS' = 'INTRO';

  formData = {
    fullName: '',
    cpf: '',
    email: '',
    password: ''
  };

  constructor(private router: Router, private data: DataService) {}

  ngOnInit(): void {
    this.loadingStage = 'INTRO';
    this.currentPhrases = ["Vamos criar sua conta..."];
    this.isLoading = false;
  }

  onSubmit(): void {
    if (!this.formData.fullName || !this.formData.cpf || !this.formData.email || !this.formData.password) {
      alert('Preencha todos os campos para continuar.');
      return;
    }

    this.data.setUserData(this.formData);
    this.isLoading = true;

    this.data.register(this.formData).subscribe({
      next: () => {
        this.isLoading = false;
        // Após registrar, vá para o signin para fazer login
        this.router.navigate(['/signin']);
      },
      error: (err) => {
        this.isLoading = false;
        alert(err?.error ?? 'Erro ao registrar.');
      }
    });
  }

  onLoadingComplete(): void {
    this.isLoading = false;
  }

  goToSignin(): void {
    this.router.navigate(['/signin']);
  }
}
