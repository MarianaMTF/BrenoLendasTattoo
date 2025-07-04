// Utility Functions for UI interactions
document.addEventListener('DOMContentLoaded', function() {
    initializeEventListeners();
    initializeFormValidations();
    initializeUIComponents();
});

// Initialize Event Listeners
function initializeEventListeners() {
    // File upload preview
    const fileUpload = document.getElementById('tattooImage');
    if (fileUpload) {
        fileUpload.addEventListener('change', handleFileUpload);
    }
    
    // Product images upload
    const productImages = document.getElementById('productImages');
    if (productImages) {
        productImages.addEventListener('change', handleProductImagesUpload);
    }
    
    // Form submissions (will be handled by Spring)
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!validateForm(this)) {
                e.preventDefault();
            }
        });
    });
    
    // Filter functionality
    const filters = document.querySelectorAll('[id$="Filter"]');
    filters.forEach(filter => {
        filter.addEventListener('change', handleFilterChange);
    });
    
    // Modal triggers
    initializeModals();
}

// Form Validation
function initializeFormValidations() {
    // Password confirmation validation
    const confirmPasswordFields = document.querySelectorAll('[id*="confirm"], [id*="Confirm"]');
    confirmPasswordFields.forEach(field => {
        field.addEventListener('blur', validatePasswordConfirmation);
    });
    
    // Email validation
    const emailFields = document.querySelectorAll('input[type="email"]');
    emailFields.forEach(field => {
        field.addEventListener('blur', validateEmail);
    });
    
    // Phone validation
    const phoneFields = document.querySelectorAll('input[type="tel"]');
    phoneFields.forEach(field => {
        field.addEventListener('input', formatPhone);
    });
    
    // Price validation
    const priceFields = document.querySelectorAll('input[step="0.01"]');
    priceFields.forEach(field => {
        field.addEventListener('input', validatePrice);
    });
}

// UI Components Initialization
function initializeUIComponents() {
    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Initialize popovers
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
    
    // Auto-hide alerts
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        if (alert.classList.contains('alert-success')) {
            setTimeout(() => {
                alert.style.opacity = '0';
                setTimeout(() => alert.remove(), 300);
            }, 3000);
        }
    });
}

// File Upload Handling
function handleFileUpload(e) {
    const file = e.target.files[0];
    if (file) {
        // Validate file type
        if (!file.type.startsWith('image/')) {
            showAlert('Por favor, selecione apenas arquivos de imagem.', 'danger');
            e.target.value = '';
            return;
        }
        
        // Validate file size (max 5MB)
        if (file.size > 5 * 1024 * 1024) {
            showAlert('O arquivo deve ter no máximo 5MB.', 'danger');
            e.target.value = '';
            return;
        }
        
        // Show preview
        const reader = new FileReader();
        reader.onload = function(e) {
            const preview = document.getElementById('imagePreview');
            if (preview) {
                preview.src = e.target.result;
                preview.style.display = 'block';
                preview.classList.add('fade-in');
            }
        };
        reader.readAsDataURL(file);
    }
}

function handleProductImagesUpload(e) {
    const files = Array.from(e.target.files);
    
    // Validate number of files
    if (files.length > 5) {
        showAlert('Você pode selecionar no máximo 5 imagens.', 'danger');
        e.target.value = '';
        return;
    }
    
    // Validate each file
    for (let file of files) {
        if (!file.type.startsWith('image/')) {
            showAlert('Por favor, selecione apenas arquivos de imagem.', 'danger');
            e.target.value = '';
            return;
        }
        
        if (file.size > 5 * 1024 * 1024) {
            showAlert('Cada arquivo deve ter no máximo 5MB.', 'danger');
            e.target.value = '';
            return;
        }
    }
    
    // Show file count
    const fileCount = document.createElement('small');
    fileCount.className = 'text-muted';
    fileCount.textContent = `${files.length} arquivo(s) selecionado(s)`;
    
    const existingCount = e.target.parentNode.querySelector('.file-count');
    if (existingCount) {
        existingCount.remove();
    }
    
    fileCount.className += ' file-count';
    e.target.parentNode.appendChild(fileCount);
}

// Form Validation Functions
function validateForm(form) {
    let isValid = true;
    const requiredFields = form.querySelectorAll('[required]');
    
    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            showFieldError(field, 'Este campo é obrigatório.');
            isValid = false;
        } else {
            clearFieldError(field);
        }
    });
    
    return isValid;
}

function validatePasswordConfirmation(e) {
    const confirmField = e.target;
    const passwordField = document.querySelector('input[type="password"]:not([id*="confirm"]):not([id*="Confirm"])');
    
    if (passwordField && confirmField.value !== passwordField.value) {
        showFieldError(confirmField, 'As senhas não coincidem.');
        return false;
    } else {
        clearFieldError(confirmField);
        return true;
    }
}

function validateEmail(e) {
    const email = e.target.value;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    
    if (email && !emailRegex.test(email)) {
        showFieldError(e.target, 'Por favor, insira um email válido.');
        return false;
    } else {
        clearFieldError(e.target);
        return true;
    }
}

function formatPhone(e) {
    let value = e.target.value.replace(/\D/g, '');
    
    if (value.length <= 11) {
        value = value.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
        if (value.length < 14) {
            value = value.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3');
        }
    }
    
    e.target.value = value;
}

function validatePrice(e) {
    const value = parseFloat(e.target.value);
    
    if (value < 0) {
        showFieldError(e.target, 'O preço deve ser maior que zero.');
        return false;
    } else {
        clearFieldError(e.target);
        return true;
    }
}

// Field Error Handling
function showFieldError(field, message) {
    clearFieldError(field);
    
    field.classList.add('is-invalid');
    
    const errorDiv = document.createElement('div');
    errorDiv.className = 'invalid-feedback';
    errorDiv.textContent = message;
    
    field.parentNode.appendChild(errorDiv);
}

function clearFieldError(field) {
    field.classList.remove('is-invalid');
    
    const errorDiv = field.parentNode.querySelector('.invalid-feedback');
    if (errorDiv) {
        errorDiv.remove();
    }
}

// Alert System
function showAlert(message, type = 'info') {
    const alertContainer = document.getElementById('alertContainer') || createAlertContainer();
    
    const alert = document.createElement('div');
    alert.className = `alert alert-${type} alert-dismissible fade show`;
    alert.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    alertContainer.appendChild(alert);
    
    // Auto-hide success alerts
    if (type === 'success') {
        setTimeout(() => {
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 300);
        }, 3000);
    }
}

function createAlertContainer() {
    const container = document.createElement('div');
    container.id = 'alertContainer';
    container.className = 'position-fixed top-0 end-0 p-3';
    container.style.zIndex = '9999';
    
    document.body.appendChild(container);
    return container;
}

// Filter Functionality
function handleFilterChange(e) {
    const filterType = e.target.id.replace('Filter', '');
    const filterValue = e.target.value.toLowerCase();
    const container = document.querySelector('[id$="Container"]');
    
    if (!container) return;
    
    const items = container.querySelectorAll('.col-md-4, .col-md-6');
    
    items.forEach(item => {
        const card = item.querySelector('.card');
        if (!card) return;
        
        let shouldShow = true;
        
        if (filterValue) {
            const cardText = card.textContent.toLowerCase();
            shouldShow = cardText.includes(filterValue);
        }
        
        if (shouldShow) {
            item.style.display = 'block';
            item.classList.add('fade-in');
        } else {
            item.style.display = 'none';
        }
    });
}

// Modal Functions
function initializeModals() {
    // Login modal
    const loginButtons = document.querySelectorAll('[onclick*="showLoginModal"]');
    loginButtons.forEach(button => {
        button.removeAttribute('onclick');
        button.addEventListener('click', showLoginModal);
    });
    
    // Register modal
    const registerButtons = document.querySelectorAll('[onclick*="showRegisterModal"]');
    registerButtons.forEach(button => {
        button.removeAttribute('onclick');
        button.addEventListener('click', showRegisterModal);
    });
}

function showLoginModal() {
    const modal = new bootstrap.Modal(document.getElementById('loginModal'));
    modal.show();
}

function showRegisterModal() {
    const modal = new bootstrap.Modal(document.getElementById('registerModal'));
    modal.show();
}

// Utility Functions
function scrollToPortfolio() {
    const portfolio = document.getElementById('portfolio');
    if (portfolio) {
        portfolio.scrollIntoView({ behavior: 'smooth' });
    }
}

function formatCurrency(value) {
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(value);
}

function formatDate(date) {
    return new Intl.DateTimeFormat('pt-BR').format(new Date(date));
}

function formatDateTime(date) {
    return new Intl.DateTimeFormat('pt-BR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    }).format(new Date(date));
}

// Loading States
function showLoading(element) {
    if (typeof element === 'string') {
        element = document.getElementById(element);
    }
    
    if (element) {
        element.disabled = true;
        const originalText = element.textContent;
        element.dataset.originalText = originalText;
        element.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Carregando...';
    }
}

function hideLoading(element) {
    if (typeof element === 'string') {
        element = document.getElementById(element);
    }
    
    if (element && element.dataset.originalText) {
        element.disabled = false;
        element.textContent = element.dataset.originalText;
        delete element.dataset.originalText;
    }
}

// Table Utilities
function sortTable(table, column, direction = 'asc') {
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    
    rows.sort((a, b) => {
        const aVal = a.cells[column].textContent.trim();
        const bVal = b.cells[column].textContent.trim();
        
        if (direction === 'asc') {
            return aVal.localeCompare(bVal);
        } else {
            return bVal.localeCompare(aVal);
        }
    });
    
    rows.forEach(row => tbody.appendChild(row));
}

// Form Reset with Animation
function resetFormWithAnimation(formId) {
    const form = document.getElementById(formId);
    if (form) {
        form.style.opacity = '0.5';
        setTimeout(() => {
            form.reset();
            clearAllFieldErrors(form);
            form.style.opacity = '1';
        }, 200);
    }
}

function clearAllFieldErrors(form) {
    const invalidFields = form.querySelectorAll('.is-invalid');
    invalidFields.forEach(field => clearFieldError(field));
    
    const errorDivs = form.querySelectorAll('.invalid-feedback');
    errorDivs.forEach(div => div.remove());
}

// Responsive Sidebar Toggle
function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    if (sidebar) {
        sidebar.classList.toggle('show');
    }
}

// Initialize responsive behavior
window.addEventListener('resize', function() {
    const sidebar = document.querySelector('.sidebar');
    if (window.innerWidth > 768 && sidebar) {
        sidebar.classList.remove('show');
    }
});

// Prevent form submission on Enter key for specific fields
document.addEventListener('keypress', function(e) {
    if (e.key === 'Enter' && e.target.tagName === 'INPUT' && e.target.type !== 'submit') {
        const form = e.target.closest('form');
        const submitButton = form?.querySelector('button[type="submit"]');
        
        if (submitButton && validateForm(form)) {
            submitButton.click();
        }
        e.preventDefault();
    }
});

// Auto-save form data to localStorage (optional)
function enableAutoSave(formId) {
    const form = document.getElementById(formId);
    if (!form) return;
    
    const inputs = form.querySelectorAll('input, textarea, select');
    
    // Load saved data
    inputs.forEach(input => {
        const savedValue = localStorage.getItem(`${formId}_${input.name}`);
        if (savedValue && input.type !== 'password') {
            input.value = savedValue;
        }
    });
    
    // Save data on change
    inputs.forEach(input => {
        input.addEventListener('change', function() {
            if (this.type !== 'password') {
                localStorage.setItem(`${formId}_${this.name}`, this.value);
            }
        });
    });
    
    // Clear saved data on successful submission
    form.addEventListener('submit', function() {
        inputs.forEach(input => {
            localStorage.removeItem(`${formId}_${input.name}`);
        });
    });
}