import { Component, Input, Output, EventEmitter, OnInit, OnDestroy, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-loading',
  standalone: true,
  imports: [CommonModule],
  // Verifique se o nome do seu HTML é loading.html ou loading.component.html
  // Se der erro de template, ajuste esta linha abaixo:
  templateUrl: './loading.html', 
  styleUrls: ['./loading.css'] 
})
export class LoadingComponent implements OnInit, OnDestroy, OnChanges {
  @Input() isLoading: boolean = false;
  
  // === AQUI ESTAVA FALTANDO ===
  // Permite receber frases personalizadas de outros componentes
  @Input() phrases: string[] = [
    "Processando...",
    "Aguarde um momento..."
  ];

  @Output() loadingComplete = new EventEmitter<void>();

  private readonly MIN_LOADING_TIME_MS = 2500; 

  currentPhrase: string = '';
  
  private phraseInterval: any;
  private timeoutId: any;

  ngOnInit(): void {
    if (this.phrases && this.phrases.length > 0) {
      this.currentPhrase = this.phrases[0];
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    // Se a lista de frases mudou (veio do pai), atualiza a frase atual
    if (changes['phrases'] && !changes['phrases'].firstChange) {
      if (this.phrases.length > 0) {
        this.currentPhrase = this.phrases[0];
      }
    }

    // Se o status de loading mudou
    if (changes['isLoading']) {
      if (changes['isLoading'].currentValue === true) {
        this.startLoadingProcess();
      } else {
        this.cleanup();
      }
    }
  }

  ngOnDestroy(): void {
    this.cleanup();
  }
  
  private cleanup(): void {
    if (this.phraseInterval) clearInterval(this.phraseInterval);
    if (this.timeoutId) clearTimeout(this.timeoutId);
  }

  private startLoadingProcess(): void {
    this.startPhraseCycle();
    this.timeoutId = setTimeout(() => {
      this.loadingComplete.emit(); 
      this.cleanup();
    }, this.MIN_LOADING_TIME_MS);
  }

  private startPhraseCycle(): void {
    let index = 0;
    // Garante que comece com a primeira frase
    this.currentPhrase = this.phrases[0];
    
    if (this.phrases.length > 1) {
      this.phraseInterval = setInterval(() => {
        index = (index + 1) % this.phrases.length;
        this.currentPhrase = this.phrases[index];
      }, 1200);
    }
  }
}