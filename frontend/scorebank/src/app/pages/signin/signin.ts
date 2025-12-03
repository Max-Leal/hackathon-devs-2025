import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { LoadingComponent } from '../../shared/loading/loading';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';

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


  // Controla a visibilidade do loading
  isLoading: boolean = false;
  
  // Controla quais frases aparecem
  currentPhrases: string[] = [];

  // Variável para saber o estágio (INTRO ou LOGIN)
  loadingStage: 'INTRO' | 'LOGIN' = 'INTRO';

  constructor(private router: Router, private fb: FormBuilder) {
    this.form = this.fb.group({
    email: ['', [Validators.required]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    rememberMe: [false],
    });
  }
  ngOnInit(): void {
    // Frase ajustada para Login (não "criar conta")
    this.currentPhrases = ["Bem-vindo de volta!"];
    this.isLoading = true;
  }

  onLoadingComplete(): void {
    this.isLoading = false; // Esconde o loading
  }
    handleSubmit() {
    if (this.form.invalid) {
      this.error = 'Preencha os campos corretamente.';
      return;
    }
    this.error = null;
    this.loading = true;

    // Simulação de login
    const { email, password, rememberMe } = this.form.value;
    setTimeout(() => {
      // Exemplo de erro vindo do backend
      if (email?.toString().includes('erro')) {
        this.error = 'Credenciais inválidas. Tente novamente.';
        this.loading = false;
        return;
      }

      // Sucesso
      this.loading = false;
      // redirecionar...
      // this.router.navigate(['/dashboard']);
    }, 1000);
  }
 OnEnter() {
  setTimeout(() => {
    this.router.navigate(['/dashboard']);
  }, 1300); // 2000 ms = 2 segundos
}

  onBack() {
    this.router.navigate([''])
  }

  onCreateAccount() {
   this.router.navigate(['/signup'])
  }
  
}