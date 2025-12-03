import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { LoadingComponent } from '../../shared/loading/loading';

@Component({
  selector: 'app-signin',
  standalone: true,
  imports: [CommonModule, LoadingComponent],
  templateUrl: './signin.html',
  styleUrls: ['./signin.css'], 
})
export class SigninComponent implements OnInit {

  // Controla a visibilidade do loading
  isLoading: boolean = false;
  
  // Controla quais frases aparecem
  currentPhrases: string[] = [];

  // Variável para saber o estágio (INTRO ou LOGIN)
  loadingStage: 'INTRO' | 'LOGIN' = 'INTRO';

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Frase ajustada para Login (não "criar conta")
    this.currentPhrases = ["Bem-vindo de volta!"];
    this.isLoading = true;
  }

  onLoadingComplete(): void {
    this.isLoading = false; // Esconde o loading
  }
}