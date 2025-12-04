import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http'; // <--- Import obrigatório
import { Observable } from 'rxjs'; // <--- Import obrigatório

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

@Injectable({
  providedIn: 'root'
})

export class DataService {
  
  id = localStorage.getItem('userId')
  private readonly API_URL = `http://localhost:8080/customers/${this.id}/score`; 

  user: UserData = { fullName: '', cpf: '', email: '', password: '' };
  financial: FinancialData = { age: null, monthlyIncome: null, profession: '', dependents: null };
  
  analysisResult: any = null;

  // Injeção do HttpClient no construtor
  constructor(private http: HttpClient) { }

  setUserData(data: UserData) { this.user = data; }
  setFinancialData(data: FinancialData) { this.financial = data; }

  // === O MÉTODO QUE FALTAVA ===
  sendAnalysis(): Observable<any> {
    const payload = {
      fullName: this.user.fullName,
      age: this.financial.age,
      email: this.user.email,
      password: this.user.password,
      cpf: this.user.cpf,
      scoreData: {
        monthlyIncome: this.financial.monthlyIncome,
        profession: this.financial.profession,
        dependents: this.financial.dependents,
        externalDebt: null, 
        fraudSuspicion: false,
        educationLevel: null,
        housingStatus: null
      }
    };

    console.log("Enviando payload para API:", payload);
    return this.http.post<any>(this.API_URL, payload);
  }
}