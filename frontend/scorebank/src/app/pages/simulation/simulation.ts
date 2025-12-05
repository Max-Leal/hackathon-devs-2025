import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-simulation',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './simulation.html',
  styleUrls: ['./simulation.css']
})
export class SimulationComponent implements OnInit {

  userId: string = '';
  
  financialData = {
    profession: '',
    monthlyIncome: null as number | null,
    monthsInCurrentJob: null as number | null
  };

  professions = [
    'Concursado',
    'Funcionário Público',
    'Militar',
    'Médico',
    'Juiz',
    'Policial',
    'Analista TI',
    'Autônomo',
    'Estudante',
    'Estagiário',
    'Uber',
    'Desempregado'
  ];

  isLoading = false;
  error = '';
  success = '';

  constructor(
    private router: Router, 
    private http: HttpClient
  ) {}

  ngOnInit() {
    // Verifica se está logado
    const userId = localStorage.getItem('userId');
    if (!userId) {
      this.router.navigate(['/signin']);
      return;
    }
    this.userId = userId;
  }

  onSubmit() {
    this.error = '';
    this.success = '';

    // Validação
    if (!this.financialData.profession || 
        !this.financialData.monthlyIncome || 
        !this.financialData.monthsInCurrentJob) {
      this.error = 'Preencha todos os campos.';
      return;
    }

    this.isLoading = true;

    // Requisição para calcular score
    this.http.post(
      `http://localhost:8080/customers/${this.userId}/score`, 
      this.financialData
    ).subscribe({
      next: (response) => {
        console.log('Score calculado com sucesso:', response);
        this.success = 'Score calculado com sucesso!';
        this.isLoading = false;

        // Redireciona para dashboard após 2 segundos
        setTimeout(() => {
          this.router.navigate(['/dashboard']);
        }, 2000);
      },
      error: (err) => {
        console.error('Erro ao calcular score:', err);
        this.isLoading = false;
        
        if (err.status === 400) {
          this.error = 'Dados inválidos. Verifique os valores informados.';
        } else if (err.status === 404) {
          this.error = 'Usuário não encontrado.';
        } else {
          this.error = 'Erro ao calcular score. Tente novamente.';
        }
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }
}