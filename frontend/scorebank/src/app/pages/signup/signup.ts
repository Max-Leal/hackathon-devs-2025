import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
// Import do componente de loading (ajuste se necessário para loading.component)
import { LoadingComponent } from '../../shared/loading/loading'; 
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, LoadingComponent], 
  templateUrl: './signup.html',
  styleUrls: ['./signup.css']
})
export class SignupComponent implements OnInit {

  // Controla a visibilidade do loading
  isLoading: boolean = false;
  
  // Controla quais frases aparecem
  currentPhrases: string[] = [];

  constructor(private router: Router) {}

  ngOnInit(): void {
    // === PASSO 1: CONFIGURA O LOADING DE ENTRADA ===
    this.currentPhrases = ["Vamos criar sua conta..."];
    this.isLoading = true; // Ativa o loading imediatamente
  }

  onLoadingComplete(): void {
    this.isLoading = false; // Esconde o loading
  }
}