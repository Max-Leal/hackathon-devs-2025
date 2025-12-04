import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { LoadingComponent } from '../../shared/loading/loading';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { DataService } from '../../shared/loading/data.service';

@Component({
  selector: 'app-signin',
  standalone: true,
  imports: [CommonModule, LoadingComponent, ReactiveFormsModule],
  templateUrl: './signin.html',
  styleUrls: ['./signin.css'],
})
export class SigninComponent implements OnInit {
  showPassword = false;
  loading = false;
  error: string | null = null;
  form!: FormGroup;

  isLoading = false;
  currentPhrases: string[] = [];
  loadingStage: 'INTRO' | 'LOGIN' = 'INTRO';

  constructor(private router: Router, private fb: FormBuilder, private data: DataService) {
    this.form = this.fb.group({
      email: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rememberMe: [false],
    });
  }

  ngOnInit(): void {
    this.currentPhrases = ["Bem-vindo de volta!"];
    this.isLoading = false; // deixa falso para não ficar bloqueando
  }

  onLoadingComplete(): void {
    this.isLoading = false;
  }

  handleSubmit() {
    if (this.form.invalid) {
      this.error = 'Preencha os campos corretamente.';
      return;
    }
    this.error = null;
    this.loading = true;

    const { email, password } = this.form.value;

    this.data.login(email, password).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/simulation']); // após login, vai para simulação
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error ?? 'Erro ao fazer login.';
      }
    });
  }
  OnEnter() {
  setTimeout(() => {
    this.router.navigate(['/dashboard']);
  }, 1300); // 2000 ms = 2 segundos
  }

  onBack() { this.router.navigate(['']); }
  onCreateAccount() { this.router.navigate(['/signup']); }
}
