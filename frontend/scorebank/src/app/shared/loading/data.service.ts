import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface UserData {
  fullName: string;
  cpf: string;
  email: string;
  password: string;
}

export interface FinancialData {
  age: number | null;
  monthlyIncome: number | null;
  profession: string;
  dependents: number | null;
}

@Injectable({ providedIn: 'root' })
export class DataService {
  // Ajuste se a porta do backend for diferente
  private readonly API_BASE = 'http://localhost:8080';
  private readonly CUSTOMERS_URL = `${this.API_BASE}/customers`;
  private readonly AUTH_URL = `${this.API_BASE}/auth`;

  user: UserData = { fullName: '', cpf: '', email: '', password: '' };
  financial: FinancialData = { age: null, monthlyIncome: null, profession: '', dependents: null };

  // Resultado da análise (ScoreResultResponse)
  analysisResult: any = null;

  constructor(private http: HttpClient) {}

  // Storage helpers para guardar ID do cliente após login
  setCustomerId(id: number) { localStorage.setItem('customerId', String(id)); }
  getCustomerId(): number | null {
    const raw = localStorage.getItem('customerId');
    return raw ? Number(raw) : null;
  }
  clearCustomerId() { localStorage.removeItem('customerId'); }

  setUserData(data: UserData) { this.user = data; }
  setFinancialData(data: FinancialData) { this.financial = data; }

  // Auth: Register
  register(data: UserData): Observable<any> {
    const payload = {
      fullName: data.fullName,
      age: this.financial.age ?? 25, // se ainda não tiver idade, default simples
      email: data.email,
      password: data.password,
      cpf: data.cpf
    };
    return this.http.post<any>(`${this.AUTH_URL}/register`, payload);
  }

  // Auth: Login → salva customerId
  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.AUTH_URL}/login`, { email, password }).pipe(
      tap((resp) => {
        if (resp?.id) this.setCustomerId(resp.id);
        // opcional: guardar nome/email
        this.user.fullName = resp?.fullName ?? this.user.fullName;
        this.user.email = resp?.email ?? email;
      })
    );
  }

  // Score: envia dados financeiros para /customers/{id}/score
  sendAnalysis(customerId: number): Observable<any> {
    const payload = {
      // O backend usa apenas estes campos do CustomerScoreDto
      monthlyIncome: this.financial.monthlyIncome,
      profession: this.financial.profession,
      dependents: this.financial.dependents,
      educationLevel: null,
      housingStatus: null
    };
    return this.http.post<any>(`${this.CUSTOMERS_URL}/${customerId}/score`, payload);
  }
}
